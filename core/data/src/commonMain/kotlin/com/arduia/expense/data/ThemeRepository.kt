package com.arduia.expense.data

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}

/** Storage-facing contract for the user's selected app theme (US-MORE-3). */
interface ThemeRepository {
    suspend fun getThemeMode(): Result<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>
}
