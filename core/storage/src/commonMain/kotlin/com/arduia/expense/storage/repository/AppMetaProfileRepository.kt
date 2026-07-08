package com.arduia.expense.storage.repository

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.PlatformKeyValueStore
import com.arduia.expense.storage.catchingResult

class AppMetaProfileRepository(
    private val store: AppMetaLocalStore,
    private val keyValueStore: PlatformKeyValueStore,
) : ProfileRepository {

    override suspend fun setDisplayName(name: String): Result<Unit> = catchingResult {
        // Write to the key-value store synchronously first — survives SQLCipher key-management
        // failures, same as setOnboardingComplete() below.
        keyValueStore.putString(KEY_DISPLAY_NAME, name)
        store.update { it.copy(displayName = name) }
        Unit
    }

    override suspend fun getDisplayName(): Result<String> {
        keyValueStore.getString(KEY_DISPLAY_NAME)?.let { return Result.Success(it) }
        // Fallback: check the encrypted DB (handles existing installs that wrote the name there).
        return catchingResult {
            val name = store.read().displayName
            if (name.isNotBlank()) {
                keyValueStore.putString(KEY_DISPLAY_NAME, name)
            }
            name
        }
    }

    override suspend fun isOnboardingComplete(): Result<Boolean> {
        // The key-value store is the fast, reliable path — immune to SQLCipher key failures.
        if (keyValueStore.getBoolean(KEY_ONBOARDING_COMPLETE, false)) return Result.Success(true)
        // Fallback: check the encrypted DB (handles existing installs that wrote the flag there).
        return catchingResult {
            val completed = store.read().onboardingCompleted
            if (completed) {
                // Migrate to the key-value store so future reads don't depend on the DB.
                keyValueStore.putBoolean(KEY_ONBOARDING_COMPLETE, true)
            }
            completed
        }
    }

    override suspend fun setOnboardingComplete(): Result<Unit> = catchingResult {
        // Write to the key-value store synchronously first — survives SQLCipher key-management failures.
        keyValueStore.putBoolean(KEY_ONBOARDING_COMPLETE, true)
        store.update { it.copy(onboardingCompleted = true) }
        Unit
    }

    companion object {
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        const val KEY_DISPLAY_NAME = "display_name"
    }
}
