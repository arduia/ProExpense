package com.arduia.expense.data

import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId

interface CategoryRepository {
    suspend fun getAll(): Result<List<Category>>

    suspend fun upsert(category: Category): Result<Unit>

    suspend fun delete(id: CategoryId): Result<Unit>
}
