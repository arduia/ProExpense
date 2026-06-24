package com.arduia.expense.data

interface ProfileRepository {
    suspend fun setDisplayName(name: String): Result<Unit>
    suspend fun getDisplayName(): Result<String>
}
