package com.arduia.expense.feature.sync

import com.arduia.expense.data.Result
import com.arduia.expense.data.SyncAccountRepository

/** US-SYNC-1: OAuth consent, then persist connection state. Never touches record data. */
class ConnectDriveAccountUseCase(
    private val authManager: DriveAuthManager,
    private val syncAccountRepository: SyncAccountRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        when (val signIn = authManager.signIn()) {
            is Result.Error -> signIn
            is Result.Success -> syncAccountRepository.setConnected(signIn.data.email)
        }
}

/** US-SYNC-6: clears local tokens + connection state only — never deletes local or remote data. */
class DisconnectDriveAccountUseCase(
    private val authManager: DriveAuthManager,
    private val syncAccountRepository: SyncAccountRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        // Local state is cleared regardless of signOut()'s outcome — signOut only clears an
        // on-device token cache (no network call), so its failure must never trap the user in an
        // apparently-still-connected state.
        authManager.signOut()
        return syncAccountRepository.setDisconnected()
    }
}
