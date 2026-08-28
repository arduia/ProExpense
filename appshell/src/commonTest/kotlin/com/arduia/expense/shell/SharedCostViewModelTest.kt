package com.arduia.expense.shell

import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.feature.sharedcost.ArchiveSharedCostUseCase
import com.arduia.expense.feature.sharedcost.CreateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.DeleteSharedCostUseCase
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Backbone coverage for bill splitting.
 *
 * Traceability: US-SHC-1 (split evenly across N people), US-SHC-2 (custom per-person shares are
 * stored as entered and never auto-rebalanced), US-SHC-3 (default participant names, with the
 * splitter as "You"), and US-SHC-5 (archive vs delete).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedCostViewModelTest {
    private fun TestScope.viewModel(sharedCosts: FakeSharedCosts = FakeSharedCosts()): SharedCostViewModel =
        SharedCostViewModel(
            sharedCostRepository = sharedCosts,
            currencySettingsRepository = FakeCurrencySettings(),
            createSharedCost = CreateSharedCostUseCase(sharedCosts) { 1_000L },
            deleteSharedCost = DeleteSharedCostUseCase(sharedCosts),
            archiveSharedCost = ArchiveSharedCostUseCase(sharedCosts),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `an equal split divides the total across the party`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("60")
            vm.onPeopleCountChange(3)

            assertEquals("$60", vm.uiState.value.totalLabel)
            assertEquals("$20", vm.uiState.value.perPersonLabel)
            assertEquals(3, vm.uiState.value.participants.size)
            assertTrue(
                vm.uiState.value.participants
                    .all { it.share == "$20" },
            )
        }

    @Test
    fun `the splitter defaults to You and the rest to numbered placeholders`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onPeopleCountChange(3)

            assertEquals(
                listOf("You", "Person 2", "Person 3"),
                vm.uiState.value.participants
                    .map { it.name },
            )
        }

    @Test
    fun `custom shares are kept as entered even when they do not sum to the total`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("100")
            vm.onPeopleCountChange(2)
            vm.onModeChange(SharedSplitMode.Custom)
            vm.onCustomShareChange(index = 0, rawShare = "30")
            vm.onCustomShareChange(index = 1, rawShare = "40")

            // 30 + 40 != 100 — the divergence is surfaced, never silently rebalanced (US-SHC-2/4).
            assertFalse(vm.uiState.value.customSumMatchesTotal)
            assertEquals(
                listOf("$30", "$40"),
                vm.uiState.value.participants
                    .map { it.share },
            )
            assertEquals("$100", vm.uiState.value.totalLabel)
        }

    @Test
    fun `matching custom shares clear the divergence hint`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("100")
            vm.onPeopleCountChange(2)
            vm.onModeChange(SharedSplitMode.Custom)
            vm.onCustomShareChange(index = 0, rawShare = "60")
            vm.onCustomShareChange(index = 1, rawShare = "40")

            assertTrue(vm.uiState.value.customSumMatchesTotal)
        }

    @Test
    fun `changing the party size re-splits evenly even in custom mode`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("90")
            vm.onModeChange(SharedSplitMode.Custom)
            vm.onCustomShareChange(index = 0, rawShare = "80")
            vm.onPeopleCountChange(3)

            assertTrue(
                vm.uiState.value.participants
                    .all { it.share == "$30" },
            )
        }

    @Test
    fun `a zero total cannot be saved`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("0")

            assertFalse(vm.uiState.value.canSave)
            assertFalse(vm.save())
        }

    /** Regression: a blank title reached the SharedCost constructor and threw instead of failing validation. */
    @Test
    fun `a blank title is refused rather than crashing the save`() =
        runTest {
            val sharedCosts = FakeSharedCosts()
            val vm = viewModel(sharedCosts)
            advanceUntilIdle()

            vm.openEditor()
            vm.onTotalChange("60")

            assertFalse(vm.uiState.value.canSave)
            assertFalse(vm.save())
            assertTrue(sharedCosts.current.isEmpty())
        }

    @Test
    fun `saving persists an equal split and closes the editor`() =
        runTest {
            val sharedCosts = FakeSharedCosts()
            val vm = viewModel(sharedCosts)
            advanceUntilIdle()

            vm.openEditor()
            vm.onTitleChange("  Dinner  ")
            vm.onTotalChange("60")
            vm.onPeopleCountChange(3)
            val saved = vm.save()
            advanceUntilIdle()

            assertTrue(saved)
            assertFalse(vm.uiState.value.isEditorOpen)
            val stored = sharedCosts.current.single()
            assertEquals("Dinner", stored.title)
            assertEquals(3, stored.participants.size)
            assertIs<SplitStrategy.EqualSplit>(stored.splitStrategy)
            assertEquals(1, vm.uiState.value.rows.size)
        }

    @Test
    fun `a blank participant name falls back to its default on save`() =
        runTest {
            val sharedCosts = FakeSharedCosts()
            val vm = viewModel(sharedCosts)
            advanceUntilIdle()

            vm.openEditor()
            vm.onTitleChange("Taxi")
            vm.onTotalChange("40")
            vm.onPeopleCountChange(2)
            vm.onNameChange(index = 1, name = "   ")
            vm.save()
            advanceUntilIdle()

            assertEquals(
                listOf("You", "Person 2"),
                sharedCosts.current
                    .single()
                    .participants
                    .map { it.name },
            )
        }

    @Test
    fun `a custom split stores per-person shares`() =
        runTest {
            val sharedCosts = FakeSharedCosts()
            val vm = viewModel(sharedCosts)
            advanceUntilIdle()

            vm.openEditor()
            vm.onTitleChange("Hotel")
            vm.onTotalChange("100")
            vm.onPeopleCountChange(2)
            vm.onModeChange(SharedSplitMode.Custom)
            vm.onCustomShareChange(index = 0, rawShare = "70")
            vm.onCustomShareChange(index = 1, rawShare = "30")
            vm.save()
            advanceUntilIdle()

            val strategy = assertIs<SplitStrategy.CustomSplit>(sharedCosts.current.single().splitStrategy)
            assertEquals(
                setOf(7_000L, 3_000L),
                strategy.shares.values
                    .map { it.amount.valueInCents }
                    .toSet(),
            )
        }

    @Test
    fun `archiving keeps the split while deleting removes it`() =
        runTest {
            val sharedCosts = FakeSharedCosts()
            val vm = viewModel(sharedCosts)
            advanceUntilIdle()
            vm.openEditor()
            vm.onTitleChange("Coffee")
            vm.onTotalChange("20")
            vm.save()
            advanceUntilIdle()
            val id =
                sharedCosts.current
                    .single()
                    .id.value

            vm.archive(sharedCostId = id)
            advanceUntilIdle()
            assertTrue(sharedCosts.current.single().isArchived)

            vm.delete(sharedCostId = id)
            advanceUntilIdle()
            assertTrue(sharedCosts.current.isEmpty())
        }
}
