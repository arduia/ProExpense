package com.arduia.expense.storage.repository

import android.content.Context

/** Synchronous peek for Activity.attachBaseContext(), which runs before Koin is available. */
fun peekLanguageTag(context: Context): String? =
    context
        .getSharedPreferences(AppMetaLocaleRepository.PREFS_NAME, Context.MODE_PRIVATE)
        .getString(AppMetaLocaleRepository.KEY_LANGUAGE_TAG, null)
