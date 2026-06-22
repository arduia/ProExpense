package com.arduia.expense.storage.repository

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toDb
import com.arduia.expense.storage.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightCategoryRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), CategoryRepository {

    private val queries get() = database.categoryQueries

    override suspend fun getAll(): Result<List<Category>> = execute {
        queries.selectAllCategories().executeAsList().map { it.toDomain() }
    }

    override suspend fun upsert(category: Category): Result<Unit> = execute {
        queries.insertCategory(
            id = category.id,
            name = category.name,
            is_custom = category.isCustom.toDb(),
        )
    }

    override suspend fun delete(id: String): Result<Unit> = execute {
        queries.deleteCategory(id)
    }
}
