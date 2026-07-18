package com.arduia.expense.data

/** Connection state shown on the "More → Google Drive Sync" settings row (US-SYNC-1). */
data class SyncAccountState(
    val isConnected: Boolean,
    val connectedAccountEmail: String?,
    val lastSyncAtEpochMillis: Long?,
)

/**
 * Non-secret Drive-sync connection state. OAuth tokens themselves are never exposed here — they
 * stay inside `feature:sync` androidMain (`EncryptedSyncTokenStore`), never surfaced as a domain
 * contract.
 */
interface SyncAccountRepository {
    suspend fun getState(): Result<SyncAccountState>

    suspend fun setConnected(accountEmail: String): Result<Unit>

    /** Clears connection state only — never touches local or remote records (US-SYNC-6). */
    suspend fun setDisconnected(): Result<Unit>

    suspend fun setLastSyncAt(epochMillis: Long): Result<Unit>
}
