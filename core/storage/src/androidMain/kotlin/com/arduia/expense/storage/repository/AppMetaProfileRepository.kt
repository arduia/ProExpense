package com.arduia.expense.storage.repository

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult

class AppMetaProfileRepository(
    private val store: AppMetaLocalStore,
) : ProfileRepository {

    override suspend fun setDisplayName(name: String): Result<Unit> = catchingResult {
        store.update { it.copy(displayName = name) }
        Unit
    }

    override suspend fun getDisplayName(): Result<String> = catchingResult {
        val snapshot = store.read()
        snapshot.displayName
    }
}
