package com.arduia.expense.data

import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getAll(): Result<List<Category>>

    suspend fun upsert(category: Category): Result<Unit>

    suspend fun delete(id: CategoryId): Result<Unit>

    suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit>

    fun observeAll(): Flow<List<Category>>
}
