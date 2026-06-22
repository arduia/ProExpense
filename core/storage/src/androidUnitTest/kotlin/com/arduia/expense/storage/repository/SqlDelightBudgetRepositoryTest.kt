package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Amount
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightBudgetRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightBudgetRepository(database, UnconfinedTestDispatcher())

    // Rule: no budget is configured by default.
    @Test
    fun default_isNull() = runTest {
        assertNull(repository.getMonthlyBudget().getOrFail())
    }

    // Rule: a set budget reads back.
    @Test
    fun set_then_get_roundTrips() = runTest {
        repository.setMonthlyBudget(Amount(120_000)).getOrFail()

        assertEquals(Amount(120_000), repository.getMonthlyBudget().getOrFail())
    }

    // Rule: setting null clears a previously set budget.
    @Test
    fun setNull_clearsBudget() = runTest {
        repository.setMonthlyBudget(Amount(120_000)).getOrFail()
        repository.setMonthlyBudget(null).getOrFail()

        assertNull(repository.getMonthlyBudget().getOrFail())
    }
}
