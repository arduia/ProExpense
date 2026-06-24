package com.arduia.expense.storage.repository

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.CategoryQueries
import com.arduia.expense.storage.mapping.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightCategoryRepository(
    private val queries: CategoryQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CategoryRepository {

    override suspend fun getAll(): Result<List<Category>> = withContext(dispatcher) {
        catchingResult { queries.selectAllCategories().executeAsList().map { it.toDomain() } }
    }

    override suspend fun upsert(category: Category): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            queries.insertCategory(
                id = category.id.value,
                name = category.name,
                is_custom = if (category.isCustom) 1L else 0L,
            )
            Unit
        }
    }

    override suspend fun delete(id: CategoryId): Result<Unit> = withContext(dispatcher) {
        catchingResult { queries.deleteCategory(id.value); Unit }
    }
}
