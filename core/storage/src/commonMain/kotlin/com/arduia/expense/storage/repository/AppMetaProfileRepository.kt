package com.arduia.expense.storage.repository

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaProfileRepository(
    private val store: AppMetaLocalStore,
    private val onboardingFlags: OnboardingFlagStore,
) : ProfileRepository {

    override suspend fun setDisplayName(name: String): Result<Unit> = catchingResult {
        // Write to the flag store synchronously first — survives SQLCipher key-management failures,
        // same as setOnboardingComplete() below.
        onboardingFlags.setDisplayName(name)
        store.update { it.copy(displayName = name) }
        Unit
    }

    override suspend fun getDisplayName(): Result<String> {
        onboardingFlags.getDisplayName()?.let { return Result.Success(it) }
        // Fallback: check the encrypted DB (handles existing installs that wrote the name there).
        return catchingResult {
            val name = store.read().displayName
            if (name.isNotBlank()) {
                onboardingFlags.setDisplayName(name)
            }
            name
        }
    }

    override suspend fun isOnboardingComplete(): Result<Boolean> {
        // The flag store is the fast, reliable path — immune to SQLCipher key failures.
        if (onboardingFlags.isOnboardingComplete()) return Result.Success(true)
        // Fallback: check the encrypted DB (handles existing installs that wrote the flag there).
        return catchingResult {
            val completed = store.read().onboardingCompleted
            if (completed) {
                // Migrate to the flag store so future reads don't depend on the DB.
                onboardingFlags.setOnboardingComplete()
            }
            completed
        }
    }

    override suspend fun setOnboardingComplete(): Result<Unit> = catchingResult {
        // Write to the flag store synchronously first — survives SQLCipher key-management failures.
        onboardingFlags.setOnboardingComplete()
        store.update { it.copy(onboardingCompleted = true) }
        Unit
    }
}
