package com.arduia.expense.feature.sync

import com.arduia.expense.data.Result

/** The connected Google account, as needed for the "Connected as {email}" settings row (US-SYNC-1). */
data class DriveAccount(
    val email: String,
)

/**
 * Platform seam for Google Sign-In + Drive `appDataFolder` OAuth authorization — implemented in
 * androidMain via Credential Manager (identity) + Google Identity Services' Authorization API
 * (Drive scope). Kept as a plain interface (no Android types in commonMain) so `feature:sync`'s
 * connect/disconnect use cases stay platform-agnostic and fakeable in tests.
 */
interface DriveAuthManager {
    /** Runs the OAuth consent flow requesting `drive.appdata` scope only; cancelling leaves no state persisted. */
    suspend fun signIn(): Result<DriveAccount>

    /** Clears locally-stored tokens only — never calls Drive or deletes any data (US-SYNC-6). */
    suspend fun signOut(): Result<Unit>

    companion object {
        /** [Result.Error.message] used by every [signIn] cancellation path (US-SYNC-1 Scenario 2). */
        const val SIGN_IN_CANCELLED_MESSAGE = "Sign-in cancelled"
    }
}
