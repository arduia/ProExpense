package com.arduia.expense.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64

class AndroidDatabaseKeyStorage(
    context: Context,
) : KeyRotationStore {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "pro_expense_key_storage",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override suspend fun getActiveKey(): ByteArray? = withContext(Dispatchers.IO) {
        prefs.getString(KEY_ACTIVE, null)?.let { decode(it) }
    }

    override suspend fun getPinSalt(): ByteArray? = withContext(Dispatchers.IO) {
        prefs.getString(KEY_SALT, null)?.let { decode(it) }
    }

    override suspend fun setPinDerivedKey(key: ByteArray, salt: ByteArray) = withContext(Dispatchers.IO) {
        prefs.edit()
            .putString(KEY_ACTIVE, encode(key))
            .putString(KEY_SALT, encode(salt))
            .putBoolean(KEY_PIN_PROTECTED, true)
            .apply()
    }

    override suspend fun setRandomKey(key: ByteArray) = withContext(Dispatchers.IO) {
        prefs.edit()
            .putString(KEY_ACTIVE, encode(key))
            .remove(KEY_SALT)
            .putBoolean(KEY_PIN_PROTECTED, false)
            .apply()
    }

    override suspend fun isPinProtected(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(KEY_PIN_PROTECTED, false)
    }

    private fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    private fun decode(value: String): ByteArray = Base64.getDecoder().decode(value)

    companion object {
        private const val KEY_ACTIVE = "active_key"
        private const val KEY_SALT = "pin_salt"
        private const val KEY_PIN_PROTECTED = "pin_protected"
    }
}
