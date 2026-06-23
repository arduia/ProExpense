package com.arduia.expense.ui.sharedcost

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.fake.FakeProfileRepository
import com.arduia.expense.fake.FakeSharedCostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedCostViewModelTest {

    private val now = 1_700_000_000_000L

    private fun TestScope.viewModel(
        repo: FakeSharedCostRepository = FakeSharedCostRepository(),
    ) = SharedCostViewModel(
        sharedCostRepository = repo,
        profileRepository = FakeProfileRepository(currency = CurrencyCode("USD"), onboardingCompleted = true),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        clock = { now },
        idGenerator = { "fixed-id" },
    )

    // Rule: stored splits surface as history items with a per-person label.
    @Test
    fun historyDerivesPerPerson() = runTest {
        val repo = FakeSharedCostRepository(
            listOf(
                SharedCost(
                    id = "s1",
                    title = "Dinner",
                    totalAmount = Amount(24_000),
                    currency = CurrencyCode("USD"),
                    participants = listOf(Participant("a", "Aiko"), Participant("b", "Ben")),
                    recordedAtEpochMillis = now,
                ),
            ),
        )
        val item = viewModel(repo).state.value.history.single()
        assertEquals(2, item.peopleCount)
        assertTrue(item.perPersonLabel.contains("120.00"))
        assertTrue(item.totalLabel.contains("240.00"))
    }

    // Rule: create persists a split with one participant per name and the parsed total.
    @Test
    fun createPersistsSplit() = runTest {
        val repo = FakeSharedCostRepository()
        val vm = viewModel(repo)

        vm.create(
            title = "Taxi",
            totalCents = 3600,
            names = listOf("You", "Lin"),
            customShareCents = null,
        )

        val saved = repo.store["fixed-id"]!!
        assertEquals("Taxi", saved.title)
        assertEquals(3600L, saved.totalAmount.valueInCents)
        assertEquals(listOf("You", "Lin"), saved.participants.map { it.name })
        assertEquals(1, vm.state.value.history.size)
    }

    // Rule: custom shares are persisted when supplied.
    @Test
    fun createKeepsCustomShares() = runTest {
        val repo = FakeSharedCostRepository()
        viewModel(repo).create(
            title = "Trip",
            totalCents = 10_000,
            names = listOf("A", "B"),
            customShareCents = listOf(7000, 3000),
        )
        assertEquals(listOf(7000L, 3000L), repo.store["fixed-id"]!!.customShareCents)
    }
}
