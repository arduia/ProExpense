package com.arduia.expense.data

interface ClearDataRepository {
    suspend fun clearExpenses(): Result<Unit>

    suspend fun clearEvents(): Result<Unit>

    suspend fun clearDebts(): Result<Unit>

    suspend fun clearSharedCosts(): Result<Unit>

    suspend fun clearAll(): Result<Unit>
}
