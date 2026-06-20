package com.arduia.expense.data

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection

interface DebtRepository {
    suspend fun getAll(): Result<List<Debt>>

    suspend fun getById(id: String): Result<Debt?>

    suspend fun upsert(debt: Debt): Result<Unit>

    suspend fun delete(id: String): Result<Unit>

    suspend fun findByPersonName(personName: String): Result<List<Debt>>
}

data class NewDebtInput(
    val personName: String,
    val amount: Amount,
    val direction: DebtDirection,
    val dueEpochMillis: Long? = null,
)
