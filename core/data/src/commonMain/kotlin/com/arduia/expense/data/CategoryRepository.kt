package com.arduia.expense.data

import com.arduia.expense.domain.Category

interface CategoryRepository {
    suspend fun getAll(): Result<List<Category>>

    suspend fun upsert(category: Category): Result<Unit>

    suspend fun delete(id: String): Result<Unit>
}
