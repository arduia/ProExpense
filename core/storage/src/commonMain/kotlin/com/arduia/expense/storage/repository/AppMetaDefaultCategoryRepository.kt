package com.arduia.expense.storage.repository

import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaDefaultCategoryRepository(
    private val store: AppMetaLocalStore,
) : DefaultCategoryRepository {

    override suspend fun getDefaultCategoryId(): Result<String?> = catchingResult {
        store.read().defaultCategoryId
    }

    override suspend fun setDefaultCategoryId(categoryId: String?): Result<Unit> = catchingResult {
        store.update { it.copy(defaultCategoryId = categoryId) }
        Unit
    }
}
