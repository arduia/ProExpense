package com.arduia.expense.storage.repository

import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.PlatformKeyValueStore
import com.arduia.expense.storage.catchingResult

class AppMetaLocaleRepository(
    private val store: AppMetaLocalStore,
    private val keyValueStore: PlatformKeyValueStore,
) : LocaleRepository {

    override suspend fun getLanguageTag(): Result<String> = catchingResult {
        val cached = keyValueStore.getString(KEY_LANGUAGE_TAG)
        if (cached != null) {
            cached
        } else {
            // Fallback: check the encrypted DB (handles existing installs that wrote it there).
            val tag = store.read().languageTag
            if (tag.isNotBlank()) keyValueStore.putString(KEY_LANGUAGE_TAG, tag)
            tag
        }
    }

    override suspend fun setLanguageTag(languageTag: String): Result<Unit> = catchingResult {
        // Write to the key-value store synchronously first — MainActivity.attachBaseContext()
        // needs to read this before Koin/the encrypted DB are available on the next Activity
        // recreation (see PlatformKeyValueStore's androidMain actual + AppMetaLocalePeek.android.kt).
        keyValueStore.putString(KEY_LANGUAGE_TAG, languageTag)
        store.update { it.copy(languageTag = languageTag) }
        Unit
    }

    companion object {
        const val KEY_LANGUAGE_TAG = "language_tag"
        const val PREFS_NAME = "onboarding_state"
    }
}
