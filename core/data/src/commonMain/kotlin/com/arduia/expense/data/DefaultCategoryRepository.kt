package com.arduia.expense.data

/** Storage-facing contract for the user's preferred default category, pre-selected in Quick Log. */
interface DefaultCategoryRepository {
    suspend fun getDefaultCategoryId(): Result<String?>

    suspend fun setDefaultCategoryId(categoryId: String?): Result<Unit>
}
