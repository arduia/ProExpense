package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlDelightDebtRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightDebtRepository(database, UnconfinedTestDispatcher())

    private fun debt(
        id: String,
        person: String,
        direction: DebtDirection = DebtDirection.I_OWE,
        settled: Boolean = false,
    ) = Debt(
        id = id,
        personName = person,
        amount = Amount(5_000),
        direction = direction,
        dueEpochMillis = 999L,
        isSettled = settled,
    )

    // Rule: a saved debt reads back field-for-field, including direction enum and settled boolean.
    @Test
    fun upsert_then_getById_roundTrips() = runTest {
        val saved = debt(id = "d1", person = "John", direction = DebtDirection.OWED_TO_ME, settled = true)
        repository.upsert(saved).getOrFail()

        assertEquals(saved, repository.getById("d1").getOrFail())
    }

    // Rule: findByPersonName returns only the rows matching that exact name.
    @Test
    fun findByPersonName_filtersByExactName() = runTest {
        repository.upsert(debt(id = "d1", person = "John")).getOrFail()
        repository.upsert(debt(id = "d2", person = "John")).getOrFail()
        repository.upsert(debt(id = "d3", person = "Jane")).getOrFail()

        val johns = repository.findByPersonName("John").getOrFail()
        assertEquals(setOf("d1", "d2"), johns.map { it.id }.toSet())
    }

    // Rule: a person with no records yields an empty (successful) list.
    @Test
    fun findByPersonName_noMatch_returnsEmpty() = runTest {
        repository.upsert(debt(id = "d1", person = "John")).getOrFail()

        assertEquals(emptyList(), repository.findByPersonName("Nobody").getOrFail())
    }

    // Rule: delete removes the targeted debt.
    @Test
    fun delete_removesDebt() = runTest {
        repository.upsert(debt(id = "d1", person = "John")).getOrFail()
        repository.delete("d1").getOrFail()

        assertEquals(emptyList(), repository.getAll().getOrFail())
    }
}
