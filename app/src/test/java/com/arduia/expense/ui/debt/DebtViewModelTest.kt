package com.arduia.expense.ui.debt

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.fake.FakeDebtRepository
import com.arduia.expense.fake.FakeProfileRepository
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DebtViewModelTest {

    private fun debt(
        id: String,
        cents: Long,
        direction: DebtDirection,
        settled: Boolean = false,
    ) = Debt(
        id = id,
        personName = "Person $id",
        amount = Amount(cents),
        direction = direction,
        isSettled = settled,
    )

    private fun TestScope.debtViewModel(
        debts: List<Debt> = emptyList(),
        repo: FakeDebtRepository = FakeDebtRepository(debts),
    ) = DebtViewModel(
        debtRepository = repo,
        profileRepository = FakeProfileRepository(currency = CurrencyCode("USD"), onboardingCompleted = true),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        idGenerator = { "new-debt" },
    )

    // Rule: debts split into Lent (owed to me) and Owe (I owe) with per-side nets.
    @Test
    fun splitsByDirection() = runTest {
        val vm = debtViewModel(
            listOf(
                debt("a", 5000, DebtDirection.OWED_TO_ME),
                debt("b", 6000, DebtDirection.OWED_TO_ME),
                debt("c", 3000, DebtDirection.I_OWE),
            ),
        )
        assertEquals(2, vm.state.value.lent.activeCount)
        assertEquals(1, vm.state.value.owe.activeCount)
        assertTrue(vm.state.value.lent.netLabel.contains("110.00"))
    }

    // Rule: settled debts move out of the active list.
    @Test
    fun settledExcludedFromActive() = runTest {
        val vm = debtViewModel(
            listOf(
                debt("a", 5000, DebtDirection.OWED_TO_ME, settled = true),
                debt("b", 6000, DebtDirection.OWED_TO_ME),
            ),
        )
        assertEquals(1, vm.state.value.lent.activeCount)
        assertEquals(1, vm.state.value.lent.settled.size)
    }

    // Rule: create persists a new debt on the chosen side.
    @Test
    fun createPersists() = runTest {
        val repo = FakeDebtRepository()
        val vm = debtViewModel(repo = repo)

        vm.create(DebtSide.Owe, "David", "30")

        val saved = repo.store["new-debt"]
        assertEquals("David", saved?.personName)
        assertEquals(DebtDirection.I_OWE, saved?.direction)
        assertEquals(3000L, saved?.amount?.valueInCents)
    }

    // Rule: marking settled flips the stored flag and refreshes the lists.
    @Test
    fun markSettledUpdatesStore() = runTest {
        val repo = FakeDebtRepository(listOf(debt("a", 5000, DebtDirection.OWED_TO_ME)))
        val vm = debtViewModel(repo = repo)

        vm.markSettled("a")

        assertTrue(repo.store["a"]!!.isSettled)
        assertEquals(0, vm.state.value.lent.activeCount)
    }
}
