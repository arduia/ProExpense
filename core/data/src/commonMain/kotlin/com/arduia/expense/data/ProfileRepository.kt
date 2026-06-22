package com.arduia.expense.data

import com.arduia.expense.domain.CurrencyCode

/**
 * On-device user profile + first-run state (no account, no server). Backed by the single
 * app_meta row. Drives Splash routing (onboarding complete?), the Home greeting (name), and the
 * home currency every entry is stamped with in the single-currency MVP.
 */
data class UserProfile(
    val name: String,
    val homeCurrency: CurrencyCode,
    val onboardingCompleted: Boolean,
)

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>

    suspend fun saveProfile(name: String, homeCurrency: CurrencyCode): Result<Unit>

    suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit>
}
