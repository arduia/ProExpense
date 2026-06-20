package com.arduia.expense.feature.history

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DebtAddViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onSave_showsOppositeSideWarning() = runTest(dispatcher) {
        val existing = Debt(
            id = "debt_1",
            personName = "John",
            amount = Amount(5000),
            direction = DebtDirection.I_OWE,
        )
        val repository = FakeDebtRepository(listOf(existing))
        var saved = false
        val viewModel = DebtAddViewModel(
            debtRepository = repository,
            isLent = true,
            scope = scope.backgroundScope,
            onSaved = { saved = true },
        )

        viewModel.onPersonNameChange("John")
        viewModel.onAmountChange("25")
        viewModel.onSave()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.oppositeSideWarning)
        assertNull(repository.lastUpserted)

        viewModel.onConfirmDespiteWarning()
        advanceUntilIdle()

        assertNotNull(repository.lastUpserted)
        assertTrue(saved)
    }
}

private class FakeDebtRepository(
    private val debts: List<Debt> = emptyList(),
) : DebtRepository {
    var lastUpserted: Debt? = null

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(debts)

    override suspend fun getById(id: String): Result<Debt?> =
        Result.Success(debts.firstOrNull { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> {
        lastUpserted = debt
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> = Result.Success(Unit)

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(debts.filter { it.personName.equals(personName, ignoreCase = true) })
}
