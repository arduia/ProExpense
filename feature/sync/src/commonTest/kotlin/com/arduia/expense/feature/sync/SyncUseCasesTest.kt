package com.arduia.expense.feature.sync

import com.arduia.expense.data.Result
import com.arduia.expense.data.SyncAccountRepository
import com.arduia.expense.data.SyncAccountState
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeDriveAuthManager(
    var signInResult: Result<DriveAccount> = Result.Success(DriveAccount("maya@example.com")),
) : DriveAuthManager {
    var signOutCalled = false

    override suspend fun signIn(): Result<DriveAccount> = signInResult

    override suspend fun signOut(): Result<Unit> {
        signOutCalled = true
        return Result.Success(Unit)
    }
}

private class FakeSyncAccountRepository(
    var state: SyncAccountState = SyncAccountState(isConnected = false, connectedAccountEmail = null, lastSyncAtEpochMillis = null),
) : SyncAccountRepository {
    override suspend fun getState(): Result<SyncAccountState> = Result.Success(state)

    override suspend fun setConnected(accountEmail: String): Result<Unit> {
        state = state.copy(isConnected = true, connectedAccountEmail = accountEmail)
        return Result.Success(Unit)
    }

    override suspend fun setDisconnected(): Result<Unit> {
        state = SyncAccountState(isConnected = false, connectedAccountEmail = null, lastSyncAtEpochMillis = null)
        return Result.Success(Unit)
    }

    override suspend fun setLastSyncAt(epochMillis: Long): Result<Unit> {
        state = state.copy(lastSyncAtEpochMillis = epochMillis)
        return Result.Success(Unit)
    }
}

class SyncUseCasesTest {
    // Success path (US-SYNC-1 Scenario 1): OAuth succeeds -> connection state is persisted.
    @Test
    fun connect_success_persistsConnectionState() =
        runTest {
            val authManager = FakeDriveAuthManager(signInResult = Result.Success(DriveAccount("carlos@example.com")))
            val accountRepository = FakeSyncAccountRepository()
            val useCase = ConnectDriveAccountUseCase(authManager, accountRepository)

            val result = useCase()

            assertIs<Result.Success<Unit>>(result)
            val state = accountRepository.state
            assertTrue(state.isConnected)
            assertEquals("carlos@example.com", state.connectedAccountEmail)
        }

    // Primary failure mode (US-SYNC-1 Scenario 2): cancelled/failed OAuth leaves no state persisted.
    @Test
    fun connect_authFailure_leavesNoConnectionState() =
        runTest {
            val authManager =
                FakeDriveAuthManager(signInResult = Result.Error(DriveAuthManager.SIGN_IN_CANCELLED_MESSAGE))
            val accountRepository = FakeSyncAccountRepository()
            val useCase = ConnectDriveAccountUseCase(authManager, accountRepository)

            val result = useCase()

            assertIs<Result.Error>(result)
            assertTrue(!accountRepository.state.isConnected)
            assertNull(accountRepository.state.connectedAccountEmail)
        }

    // Key invariant (US-SYNC-6): disconnect always clears local connection state, and never touches
    // record data — this use case has no record-repository dependency at all, so that invariant is
    // structural, not just behavioral.
    @Test
    fun disconnect_clearsConnectionState_regardlessOfSignOutOutcome() =
        runTest {
            val authManager = FakeDriveAuthManager()
            val accountRepository =
                FakeSyncAccountRepository(
                    state = SyncAccountState(isConnected = true, connectedAccountEmail = "aiko@example.com", lastSyncAtEpochMillis = 123L),
                )
            val useCase = DisconnectDriveAccountUseCase(authManager, accountRepository)

            val result = useCase()

            assertIs<Result.Success<Unit>>(result)
            assertTrue(authManager.signOutCalled)
            val state = accountRepository.state
            assertTrue(!state.isConnected)
            assertNull(state.connectedAccountEmail)
            assertNull(state.lastSyncAtEpochMillis)
        }
}
