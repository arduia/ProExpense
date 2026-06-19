package com.arduia.expense.feature.logging

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Category
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryCategoryRepository : CategoryRepository {
    private val mutex = Mutex()
    private val categories = mutableListOf<Category>()

    init {
        val defaults = defaultLogCategories.map { Category(id = it.id, name = it.label, isCustom = false) }
        val customs = customLogCategories.map { Category(id = it.id, name = it.label, isCustom = true) }
        categories.addAll(defaults + customs)
    }

    override suspend fun getAll(): Result<List<Category>> = mutex.withLock {
        Result.Success(categories.toList())
    }

    override suspend fun upsert(category: Category): Result<Unit> = mutex.withLock {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index >= 0) {
            categories[index] = category
        } else {
            categories.add(category)
        }
        Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> = mutex.withLock {
        categories.removeAll { it.id == id && it.isCustom }
        Result.Success(Unit)
    }
}
