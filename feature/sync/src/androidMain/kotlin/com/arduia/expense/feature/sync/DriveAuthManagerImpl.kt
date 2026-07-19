package com.arduia.expense.feature.sync

import android.content.Context
import android.util.Log
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
        Log.d(TAG, "signIn: starting")
        val activity =
            activity ?: run {
                Log.e(TAG, "signIn: no foreground activity set — SyncConnectFlow must set it before calling signIn()")
                return Result.Error("No foreground activity to launch sign-in from")
            }
        return try {
            val email = fetchAccountEmail(activity)
            Log.d(TAG, "signIn: Credential Manager returned identity for $email")
            authorizeDriveAppDataScope(activity)
            Log.d(TAG, "signIn: Drive appdata scope authorized, access token stored")
            Log.i(TAG, "signIn: success, connected as $email")
            Result.Success(DriveAccount(email))
        } catch (e: DriveSignInCancelledException) {
            // Cancelling leaves no partial state — nothing was persisted (US-SYNC-1 Scenario 2).
            Log.w(TAG, "signIn: cancelled by user, no state persisted")
            Result.Error("Sign-in cancelled", e)
        } catch (e: Exception) {
            Log.e(TAG, "signIn: failed", e)
            Result.Error(e.message ?: "Google Drive sign-in failed", e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        Log.d(TAG, "signOut: clearing local sync tokens (local-only, no Drive/network call)")
        return try {
            tokenStore.clear()
            Log.i(TAG, "signOut: success")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "signOut: failed to clear sync tokens", e)
            Result.Error(e.message ?: "Failed to clear sync tokens", e)
        }
    }

    private suspend fun fetchAccountEmail(activity: ComponentActivity): String {
        Log.d(TAG, "fetchAccountEmail: launching Credential Manager Sign in with Google")
        val option = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val response = CredentialManager.create(context).getCredential(activity, request)
        val credential = response.credential
        Log.d(TAG, "fetchAccountEmail: received credential of type ${credential.type}")
        require(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
        ) { "Unexpected credential type from Credential Manager" }
        return GoogleIdTokenCredential.createFrom(credential.data).id
    }

    private suspend fun authorizeDriveAppDataScope(activity: ComponentActivity): String {
        Log.d(TAG, "authorizeDriveAppDataScope: requesting scope $DRIVE_APPDATA_SCOPE")
        val request =
            AuthorizationRequest
                .builder()
                .setRequestedScopes(listOf(Scope(DRIVE_APPDATA_SCOPE)))
                .build()
        val result = requestAuthorization(activity, request)
        val accessToken =
            result.accessToken ?: run {
                Log.e(TAG, "authorizeDriveAppDataScope: authorization succeeded but no access token was returned")
                error("Authorization succeeded without an access token")
            }
        tokenStore.writeAccessToken(accessToken)
        return accessToken
    }

    private suspend fun requestAuthorization(
        activity: ComponentActivity,
        request: AuthorizationRequest,
    ): AuthorizationResult =
        suspendCancellableCoroutine { continuation ->
            val client = Identity.getAuthorizationClient(activity)
            Log.d(TAG, "requestAuthorization: calling AuthorizationClient.authorize()")
            client
                .authorize(request)
                .addOnSuccessListener { result ->
                    if (!result.hasResolution()) {
                        Log.d(TAG, "requestAuthorization: onSuccess — already authorized, no consent UI needed")
                        continuation.resume(result)
                        return@addOnSuccessListener
                    }
                    Log.d(TAG, "requestAuthorization: onSuccess — needs resolution, consent UI required")
                    val pendingIntent = result.pendingIntent
                    if (pendingIntent == null) {
                        Log.e(TAG, "requestAuthorization: hasResolution() true but pendingIntent is null")
                        val error = IllegalStateException("Authorization needs resolution but has no pendingIntent")
                        continuation.resumeWithException(error)
                        return@addOnSuccessListener
                    }
                    val launcher =
                        activity.activityResultRegistry.register(
                            "drive_sync_authorization_${System.currentTimeMillis()}",
                            ActivityResultContracts.StartIntentSenderForResult(),
                        ) { activityResult ->
                            Log.d(
                                TAG,
                                "requestAuthorization: consent UI returned resultCode=${activityResult.resultCode}",
                            )
                            val data = activityResult.data
                            if (activityResult.resultCode != android.app.Activity.RESULT_OK || data == null) {
                                Log.w(TAG, "requestAuthorization: consent UI cancelled or returned no data")
                                continuation.resumeWithException(DriveSignInCancelledException())
                                return@register
                            }
                            try {
                                val resolved = client.getAuthorizationResultFromIntent(data)
                                Log.d(TAG, "requestAuthorization: resolved AuthorizationResult from consent UI intent")
                                continuation.resume(resolved)
                            } catch (e: Exception) {
                                Log.e(TAG, "requestAuthorization: failed to resolve AuthorizationResult from intent", e)
                                continuation.resumeWithException(e)
                            }
                        }
                    Log.d(TAG, "requestAuthorization: launching consent UI intent sender")
                    launcher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                }.addOnFailureListener { error ->
                    Log.e(TAG, "requestAuthorization: AuthorizationClient.authorize() failed", error)
                    continuation.resumeWithException(error)
                }
        }

    private class DriveSignInCancelledException : Exception("Sign-in was cancelled")

    private companion object {
        const val TAG = "DriveAuthManager"
        const val DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata"
    }
}
