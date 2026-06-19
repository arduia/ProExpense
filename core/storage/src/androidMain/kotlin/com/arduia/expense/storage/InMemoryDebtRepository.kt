package com.arduia.expense.storage

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Debt

class InMemoryDebtRepository(
    private val store: InMemoryDataStore,
) : DebtRepository {
    override suspend fun getAll(): Result<List<Debt>> = Result.Success(store.allDebts())

    override suspend fun getById(id: String): Result<Debt?> =
        Result.Success(store.allDebts().firstOrNull { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> {
        val existing = store.allDebts().any { it.id == debt.id }
        if (existing) store.updateDebt(debt) else store.insertDebt(debt)
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        store.deleteDebt(id)
        return Result.Success(Unit)
    }

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> {
        val matches = store.allDebts().filter {
            it.personName.equals(personName.trim(), ignoreCase = true)
        }
        return Result.Success(matches)
    }
}
