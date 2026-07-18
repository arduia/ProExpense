package com.arduia.expense.feature.sync

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.arduia.expense.data.Result
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Google Sign-In (identity, for the "Connected as {email}" label) + Drive `appDataFolder`
 * incremental authorization (Google Identity Services' Authorization API — the modern replacement
 * for the deprecated `GoogleSignInClient` scope-request flow).
 *
 * **Unverified in this environment**: exercising this end-to-end requires a real Google Cloud
 * project with an OAuth 2.0 **Web** client ID (Credential Manager and the Authorization API both
 * require the web client type, even for an Android app — see
 * [R.string.sync_google_oauth_client_id]) and a physical device/emulator with Play Services. Both
 * are unavailable in this build environment; this class compiles against the documented API shape
 * but has not been runtime-verified.
 *
 * [activity] must be set by the hosting Compose screen (`SyncConnectFlow`) before calling [signIn]
 * — Credential Manager and the Authorization API both need a foreground Activity to show UI, which
 * a Koin-scoped singleton has no way to obtain on its own.
 */
class DriveAuthManagerImpl(
    private val context: Context,
    private val tokenStore: EncryptedSyncTokenStore,
    private val serverClientId: String,
) : DriveAuthManager {
    var activity: ComponentActivity? = null

    override suspend fun signIn(): Result<DriveAccount> {
        val activity = activity ?: return Result.Error("No foreground activity to launch sign-in from")
        return try {
            val email = fetchAccountEmail(activity)
            val accessToken = authorizeDriveAppDataScope(activity)
            tokenStore.writeAccessToken(accessToken)
            Result.Success(DriveAccount(email))
        } catch (e: DriveSignInCancelledException) {
            // Cancelling leaves no partial state — nothing was persisted (US-SYNC-1 Scenario 2).
            Result.Error("Sign-in cancelled", e)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Google Drive sign-in failed", e)
        }
    }

    override suspend fun signOut(): Result<Unit> =
        try {
            tokenStore.clear()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to clear sync tokens", e)
        }

    private suspend fun fetchAccountEmail(activity: ComponentActivity): String {
        val option = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val response = CredentialManager.create(context).getCredential(activity, request)
        val credential = response.credential
        require(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
        ) { "Unexpected credential type from Credential Manager" }
        return GoogleIdTokenCredential.createFrom(credential.data).id
    }

    private suspend fun authorizeDriveAppDataScope(activity: ComponentActivity): String {
        val request =
            AuthorizationRequest
                .builder()
                .setRequestedScopes(listOf(Scope(DRIVE_APPDATA_SCOPE)))
                .build()
        val result = requestAuthorization(activity, request)
        return result.accessToken ?: error("Authorization succeeded without an access token")
    }

    private suspend fun requestAuthorization(
        activity: ComponentActivity,
        request: AuthorizationRequest,
    ): AuthorizationResult =
        suspendCancellableCoroutine { continuation ->
            val client = Identity.getAuthorizationClient(activity)
            client
                .authorize(request)
                .addOnSuccessListener { result ->
                    if (!result.hasResolution()) {
                        continuation.resume(result)
                        return@addOnSuccessListener
                    }
                    val pendingIntent = result.pendingIntent
                    if (pendingIntent == null) {
                        val error = IllegalStateException("Authorization needs resolution but has no pendingIntent")
                        continuation.resumeWithException(error)
                        return@addOnSuccessListener
                    }
                    val launcher =
                        activity.activityResultRegistry.register(
                            "drive_sync_authorization_${System.currentTimeMillis()}",
                            ActivityResultContracts.StartIntentSenderForResult(),
                        ) { activityResult ->
                            val data = activityResult.data
                            if (activityResult.resultCode != android.app.Activity.RESULT_OK || data == null) {
                                continuation.resumeWithException(DriveSignInCancelledException())
                                return@register
                            }
                            try {
                                val resolved = client.getAuthorizationResultFromIntent(data)
                                continuation.resume(resolved)
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                            }
                        }
                    launcher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                }.addOnFailureListener { error -> continuation.resumeWithException(error) }
        }

    private class DriveSignInCancelledException : Exception("Sign-in was cancelled")

    private companion object {
        const val DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata"
    }
}
