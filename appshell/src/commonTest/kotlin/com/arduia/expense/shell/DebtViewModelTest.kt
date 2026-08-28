package com.arduia.expense.shell

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.feature.debt.AggregateDebtsUseCase
import com.arduia.expense.feature.debt.CheckDebtConflictUseCase
import com.arduia.expense.feature.debt.CreateDebtUseCase
import com.arduia.expense.feature.debt.DeleteDebtUseCase
import com.arduia.expense.feature.debt.SettleDebtUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Backbone coverage for the lent/owed ledger.
 *
 * Traceability: US-DEBT-1 (record a debt in either direction), US-DEBT-2 (net total per direction,
 * active vs settled), US-DEBT-3 (delete clears the link on any expense tagged to it), and
 * US-DEBT-4 (warn when the same person already has an active debt on the opposite side).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DebtViewModelTest {
    private fun debt(
        id: String,
        person: String,
        cents: Long,
        direction: DebtDirection,
        settled: Boolean = false,
    ) = Debt(
        id = DebtId(id),
        personName = person,
        money = money(cents),
        direction = direction,
        isSettled = settled,
        recordedAtEpochMillis = 0L,
    )

    private fun TestScope.viewModel(debts: FakeDebts): DebtViewModel =
        DebtViewModel(
            debtRepository = debts,
            currencySettingsRepository = FakeCurrencySettings(),
            actions =
                DebtActions(
                    aggregate = AggregateDebtsUseCase(),
                    create = CreateDebtUseCase(debts) { 1_000L },
                    settle = SettleDebtUseCase(debts),
                    delete = DeleteDebtUseCase(debts, FakeRecords()),
                    checkConflict = CheckDebtConflictUseCase(debts),
                ),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `it opens on money owed to the user and nets only active debts`() =
        runTest {
            val debts =
                FakeDebts(
                    listOf(
                        debt("a", "Ben", 3_000, DebtDirection.OWED_TO_ME),
                        debt("b", "Cara", 2_000, DebtDirection.OWED_TO_ME),
                        debt("c", "Dee", 9_900, DebtDirection.OWED_TO_ME, settled = true),
                        debt("d", "Eve", 5_000, DebtDirection.I_OWE),
                    ),
                )
            val vm = viewModel(debts)

            advanceUntilIdle()

            assertTrue(vm.uiState.value.isLentTab)
            assertEquals("$50", vm.uiState.value.netLabel)
            assertEquals(2, vm.uiState.value.active.size)
            assertEquals(1, vm.uiState.value.settled.size)
        }

    @Test
    fun `switching direction re-nets against the other side`() =
        runTest {
            val debts =
                FakeDebts(
                    listOf(
                        debt("a", "Ben", 3_000, DebtDirection.OWED_TO_ME),
                        debt("d", "Eve", 5_000, DebtDirection.I_OWE),
                    ),
                )
            val vm = viewModel(debts)
            advanceUntilIdle()

            vm.onDirectionChange(DebtDirection.I_OWE)
            advanceUntilIdle()

            assertFalse(vm.uiState.value.isLentTab)
            assertEquals("$50", vm.uiState.value.netLabel)
            assertEquals(
                listOf("Eve"),
                vm.uiState.value.active
                    .map { it.personName },
            )
        }

    @Test
    fun `creating records against the currently viewed direction`() =
        runTest {
            val debts = FakeDebts()
            val vm = viewModel(debts)
            advanceUntilIdle()
            vm.onDirectionChange(DebtDirection.I_OWE)

            val created =
                vm.create(
                    personName = "  Ben  ",
                    rawAmount = "25",
                    dueEpochMillis = null,
                    note = null,
                    recordAsTransaction = false,
                )
            advanceUntilIdle()

            assertTrue(created)
            val stored = debts.current.single()
            assertEquals(DebtDirection.I_OWE, stored.direction)
            assertEquals("Ben", stored.personName)
        }

    @Test
    fun `a non-positive amount is refused`() =
        runTest {
            val debts = FakeDebts()
            val vm = viewModel(debts)
            advanceUntilIdle()

            val created =
                vm.create(
                    personName = "Ben",
                    rawAmount = "0",
                    dueEpochMillis = null,
                    note = null,
                    recordAsTransaction = false,
                )

            assertFalse(created)
            assertTrue(debts.current.isEmpty())
        }

    @Test
    fun `an opposite-direction debt for the same person raises the conflict warning`() =
        runTest {
            val debts = FakeDebts(listOf(debt("a", "Ben", 3_000, DebtDirection.I_OWE)))
            val vm = viewModel(debts)
            advanceUntilIdle()

            vm.checkConflict(personName = "  ben  ")

            assertTrue(vm.uiState.value.conflictWarning)

            vm.clearConflictWarning()
            assertFalse(vm.uiState.value.conflictWarning)
        }

    @Test
    fun `no warning when the existing debt is on the same side`() =
        runTest {
            val debts = FakeDebts(listOf(debt("a", "Ben", 3_000, DebtDirection.OWED_TO_ME)))
            val vm = viewModel(debts)
            advanceUntilIdle()

            vm.checkConflict(personName = "Ben")

            assertFalse(vm.uiState.value.conflictWarning)
        }

    @Test
    fun `settling moves a debt out of the active bucket and out of the net`() =
        runTest {
            val debts = FakeDebts(listOf(debt("a", "Ben", 3_000, DebtDirection.OWED_TO_ME)))
            val vm = viewModel(debts)
            advanceUntilIdle()

            vm.settle(debtId = "a")
            advanceUntilIdle()

            assertTrue(
                vm.uiState.value.active
                    .isEmpty(),
            )
            assertEquals(1, vm.uiState.value.settled.size)
            assertEquals("$0", vm.uiState.value.netLabel)
        }

    @Test
    fun `deleting removes the debt entirely`() =
        runTest {
            val debts = FakeDebts(listOf(debt("a", "Ben", 3_000, DebtDirection.OWED_TO_ME)))
            val vm = viewModel(debts)
            advanceUntilIdle()

            vm.delete(debtId = "a")
            advanceUntilIdle()

            assertTrue(debts.current.isEmpty())
            assertTrue(vm.uiState.value.isEmpty)
        }
}
