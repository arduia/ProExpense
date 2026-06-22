package com.arduia.expense.data

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Supplies the SQLCipher passphrase (A2/C6 "random key at first launch"). A 32-byte random key is
 * generated once and stored in Keystore-backed [EncryptedSharedPreferences] — never in the DB and
 * never in plaintext prefs. PIN rotation (out of scope here) will later re-key off this seed.
 */
class DatabaseKeyProvider(context: Context) {

    private val appContext = context.applicationContext

    fun getOrCreateKey(): ByteArray {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            appContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        prefs.getString(KEY_PASSPHRASE, null)?.let { return Base64.decode(it, Base64.NO_WRAP) }

        val key = ByteArray(KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString(KEY_PASSPHRASE, Base64.encodeToString(key, Base64.NO_WRAP)).apply()
        return key
    }

    private companion object {
        const val PREFS_NAME = "proexpense_secure_prefs"
        const val KEY_PASSPHRASE = "db_passphrase"
        const val KEY_SIZE_BYTES = 32
    }
}
