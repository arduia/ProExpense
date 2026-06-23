package com.arduia.expense.data

import com.arduia.expense.domain.SharedCost

interface SharedCostRepository {
    suspend fun getAll(): Result<List<SharedCost>>

    suspend fun upsert(sharedCost: SharedCost): Result<Unit>

    suspend fun delete(id: String): Result<Unit>
}
