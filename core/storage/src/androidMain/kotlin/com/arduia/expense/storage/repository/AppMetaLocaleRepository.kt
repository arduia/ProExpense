package com.arduia.expense.storage.repository

import android.content.Context
import android.content.SharedPreferences
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaLocaleRepository(
    private val store: AppMetaLocalStore,
    private val prefs: SharedPreferences,
) : LocaleRepository {

    override suspend fun getLanguageTag(): Result<String> = catchingResult {
        val cached = prefs.getString(KEY_LANGUAGE_TAG, null)
        if (cached != null) {
            cached
        } else {
            // Fallback: check the encrypted DB (handles existing installs that wrote it there).
            val tag = store.read().languageTag
            if (tag.isNotBlank()) prefs.edit().putString(KEY_LANGUAGE_TAG, tag).commit()
            tag
        }
    }

    override suspend fun setLanguageTag(languageTag: String): Result<Unit> = catchingResult {
        // Write to SharedPrefs synchronously first — MainActivity.attachBaseContext() needs to
        // read this before Koin/the encrypted DB are available on the next Activity recreation.
        prefs.edit().putString(KEY_LANGUAGE_TAG, languageTag).commit()
        store.update { it.copy(languageTag = languageTag) }
        Unit
    }

    companion object {
        const val KEY_LANGUAGE_TAG = "language_tag"
        const val PREFS_NAME = "onboarding_state"

        /** Synchronous peek for Activity.attachBaseContext(), which runs before Koin is available. */
        fun peekLanguageTag(context: Context): String? =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE_TAG, null)
    }
}
