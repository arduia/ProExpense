package com.arduia.expense.feature.sync

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores the Drive OAuth access token, Keystore-wrapped (AES/GCM) at rest — same pattern as
 * `AndroidDatabaseKeyManager` (core:storage), reused here rather than introducing
 * `androidx.security.crypto`'s `EncryptedSharedPreferences` as a second, unprecedented encryption
 * mechanism for a single string value.
 */
class EncryptedSyncTokenStore(
    context: Context,
) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Never log the token itself (even at debug level) — only presence/outcome.
    fun readAccessToken(): String? {
        val ivB64 = prefs.getString(PREF_IV, null)
        val payloadB64 = prefs.getString(PREF_PAYLOAD, null)
        if (ivB64 == null || payloadB64 == null) {
            Log.d(TAG, "readAccessToken: no token stored")
            return null
        }
        val iv = Base64.decode(ivB64, Base64.NO_WRAP)
        val payload = Base64.decode(payloadB64, Base64.NO_WRAP)
        val cipher =
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, wrappingKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            }
        val token = cipher.doFinal(payload).decodeToString()
        Log.d(TAG, "readAccessToken: decrypted stored token")
        return token
    }

    fun writeAccessToken(token: String) {
        Log.d(TAG, "writeAccessToken: encrypting and storing new token")
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, wrappingKey()) }
        val payload = cipher.doFinal(token.encodeToByteArray())
        prefs
            .edit()
            .putString(PREF_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(PREF_PAYLOAD, Base64.encodeToString(payload, Base64.NO_WRAP))
            .apply()
        Log.d(TAG, "writeAccessToken: stored")
    }

    /** US-SYNC-6: local-only — never calls Drive, never touches record data. */
    fun clear() {
        Log.d(TAG, "clear: removing stored token (local-only)")
        prefs
            .edit()
            .remove(PREF_IV)
            .remove(PREF_PAYLOAD)
            .apply()
    }

    private fun wrappingKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(WRAP_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec
                .Builder(
                    WRAP_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build(),
        )
        return generator.generateKey()
    }

    private companion object {
        const val TAG = "EncryptedSyncTokenStore"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val WRAP_KEY_ALIAS = "proexpense_sync_token_wrap"
        const val PREFS_NAME = "proexpense_sync_secure_store"
        const val PREF_IV = "sync_token_iv"
        const val PREF_PAYLOAD = "sync_token_payload"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_BITS = 128
    }
}
