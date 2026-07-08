package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.storage.catchingResult

class AppMetaThemeRepository(
    private val store: AppMetaLocalStore,
) : ThemeRepository {

    override suspend fun getThemeMode(): Result<ThemeMode> = catchingResult {
        store.read().themeMode.toThemeModeOrDefault()
    }

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> = catchingResult {
        store.update { it.copy(themeMode = mode.name.lowercase()) }
        Unit
    }

    private fun String.toThemeModeOrDefault(): ThemeMode =
        ThemeMode.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: ThemeMode.DARK
}
