package com.arduia.expense.storage.repository

import android.content.SharedPreferences
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaProfileRepository(
    private val store: AppMetaLocalStore,
    private val onboardingPrefs: SharedPreferences,
) : ProfileRepository {

    override suspend fun setDisplayName(name: String): Result<Unit> = catchingResult {
        // Write to SharedPrefs synchronously first — survives SQLCipher key-management failures,
        // same as setOnboardingComplete() below.
        onboardingPrefs.edit().putString(KEY_DISPLAY_NAME, name).commit()
        store.update { it.copy(displayName = name) }
        Unit
    }

    override suspend fun getDisplayName(): Result<String> {
        onboardingPrefs.getString(KEY_DISPLAY_NAME, null)?.let { return Result.Success(it) }
        // Fallback: check the encrypted DB (handles existing installs that wrote the name there).
        return catchingResult {
            val name = store.read().displayName
            if (name.isNotBlank()) {
                onboardingPrefs.edit().putString(KEY_DISPLAY_NAME, name).commit()
            }
            name
        }
    }

    override suspend fun isOnboardingComplete(): Result<Boolean> {
        // SharedPrefs is the fast, reliable path — immune to SQLCipher key failures.
        if (onboardingPrefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)) return Result.Success(true)
        // Fallback: check the encrypted DB (handles existing installs that wrote the flag there).
        return catchingResult {
            val completed = store.read().onboardingCompleted
            if (completed) {
                // Migrate to SharedPrefs so future reads don't depend on the DB.
                onboardingPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).commit()
            }
            completed
        }
    }

    override suspend fun setOnboardingComplete(): Result<Unit> = catchingResult {
        // Write to SharedPrefs synchronously first — survives SQLCipher key-management failures.
        onboardingPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).commit()
        store.update { it.copy(onboardingCompleted = true) }
        Unit
    }

    companion object {
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        const val KEY_DISPLAY_NAME = "display_name"
    }
}
