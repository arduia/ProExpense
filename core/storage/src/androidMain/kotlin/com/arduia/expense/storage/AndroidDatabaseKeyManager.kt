package com.arduia.expense.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores the 32-byte SQLCipher passphrase wrapped by an AES/GCM key held in the Android Keystore.
 *
 * Raw Keystore (not EncryptedSharedPreferences) is used deliberately: a later phase needs
 * `setUserAuthenticationRequired(true)` on the wrapping key for biometric-gated unwrap, which the
 * higher-level crypto helper does not expose (design §5.2). The wrapping key created here has no
 * auth gate — that is the first-launch, no-PIN bootstrap case.
 */
class AndroidDatabaseKeyManager(context: Context) : DatabaseKeyManager {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getOrCreateDatabaseKey(): ByteArray {
        readWrappedKey()?.let { return it }
        val key = ByteArray(KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        persistWrappedKey(key)
        return key
    }

    private fun readWrappedKey(): ByteArray? {
        val ivB64 = prefs.getString(PREF_IV, null) ?: return null
        val payloadB64 = prefs.getString(PREF_PAYLOAD, null) ?: return null
        val iv = Base64.decode(ivB64, Base64.NO_WRAP)
        val payload = Base64.decode(payloadB64, Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, wrappingKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        }
        return cipher.doFinal(payload)
    }

    private fun persistWrappedKey(key: ByteArray) {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, wrappingKey())
        }
        val payload = cipher.doFinal(key)
        prefs.edit()
            .putString(PREF_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(PREF_PAYLOAD, Base64.encodeToString(payload, Base64.NO_WRAP))
            .apply()
    }

    private fun wrappingKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(WRAP_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)
            ?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                WRAP_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build(),
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val WRAP_KEY_ALIAS = "proexpense_db_key_wrap"
        const val PREFS_NAME = "proexpense_secure_store"
        const val PREF_IV = "db_key_iv"
        const val PREF_PAYLOAD = "db_key_payload"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_SIZE_BYTES = 32
        const val GCM_TAG_BITS = 128
    }
}
