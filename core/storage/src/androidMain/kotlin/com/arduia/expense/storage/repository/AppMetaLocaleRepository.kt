package com.arduia.expense.storage.repository

import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaLocaleRepository(
    private val store: AppMetaLocalStore,
) : LocaleRepository {

    override suspend fun getLanguageTag(): Result<String> = catchingResult {
        store.read().languageTag
    }

    override suspend fun setLanguageTag(languageTag: String): Result<Unit> = catchingResult {
        store.update { it.copy(languageTag = languageTag) }
        Unit
    }
}
