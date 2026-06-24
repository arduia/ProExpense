package com.arduia.expense.data

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Money
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    suspend fun getAll(): Result<List<Debt>>

    suspend fun getById(id: DebtId): Result<Debt?>

    suspend fun upsert(debt: Debt): Result<Unit>

    suspend fun delete(id: DebtId): Result<Unit>

    suspend fun findByPersonName(personName: String): Result<List<Debt>>

    /** Live, ordered (by person name) view of every debt — backs auto-updating Debt screens. */
    fun observeAll(): Flow<List<Debt>>
}

data class NewDebtInput(
    val personName: String,
    val money: Money,
    val direction: DebtDirection,
    val dueEpochMillis: Long? = null,
)
