package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.SyncAccountRepository
import com.arduia.expense.data.SyncAccountState
import com.arduia.expense.storage.catchingResult

class AppMetaSyncRepository(
    private val store: AppMetaLocalStore,
) : SyncAccountRepository {
    override suspend fun getState(): Result<SyncAccountState> =
        catchingResult {
            val snapshot = store.read()
            SyncAccountState(
                isConnected = snapshot.syncConnected,
                connectedAccountEmail = snapshot.syncAccountEmail,
                lastSyncAtEpochMillis = snapshot.syncLastSyncedAt,
            )
        }

    override suspend fun setConnected(accountEmail: String): Result<Unit> =
        catchingResult {
            store.update { it.copy(syncConnected = true, syncAccountEmail = accountEmail) }
            Unit
        }

    // Disconnect only clears connection state — never touches finance_record/tombstone rows or
    // remote Drive files (US-SYNC-6 Business Rule).
    override suspend fun setDisconnected(): Result<Unit> =
        catchingResult {
            store.update {
                it.copy(syncConnected = false, syncAccountEmail = null, syncLastSyncedAt = null)
            }
            Unit
        }

    override suspend fun setLastSyncAt(epochMillis: Long): Result<Unit> =
        catchingResult {
            store.update { it.copy(syncLastSyncedAt = epochMillis) }
            Unit
        }
}
