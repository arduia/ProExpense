package com.arduia.expense.data

interface ProfileRepository {
    suspend fun setDisplayName(name: String): Result<Unit>

    suspend fun getDisplayName(): Result<String>

    suspend fun isOnboardingComplete(): Result<Boolean>

    suspend fun setOnboardingComplete(): Result<Unit>
}
