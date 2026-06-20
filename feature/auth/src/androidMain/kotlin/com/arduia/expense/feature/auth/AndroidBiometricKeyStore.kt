package com.arduia.expense.feature.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class AndroidBiometricKeyStore(
    context: Context,
) : BiometricKeyStore {
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    override fun wrapKey(key: ByteArray): ByteArray {
        deleteKey()
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()
        generator.init(spec)
        generator.generateKey()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(KEY_ALIAS, null))
        val encrypted = cipher.doFinal(key)
        val iv = cipher.iv
        return iv.size.toByte().let { sizeByte ->
            byteArrayOf(sizeByte) + iv + encrypted
        }
    }

    override fun unwrapKey(wrapped: ByteArray): ByteArray? {
        if (wrapped.isEmpty()) return null
        val ivSize = wrapped[0].toInt() and 0xFF
        if (wrapped.size <= ivSize + 1) return null
        val iv = wrapped.sliceArray(1 until ivSize + 1)
        val encrypted = wrapped.sliceArray(ivSize + 1 until wrapped.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null), spec)
        return cipher.doFinal(encrypted)
    }

    private fun deleteKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
        }
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "pro_expense_biometric_db_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}
