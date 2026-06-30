package com.arduia.expense.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.arduia.expense.data.Result
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import java.security.KeyStore

class AndroidIntegrityKeyManager : IntegrityKeyManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyAlias = "pro_expense_integrity_hmac"

    init {
        ensureKeyExists()
    }

    private fun ensureKeyExists() {
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
            keyGenerator.init(
                KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_SIGN)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    override suspend fun sign(canonical: String): Result<String> = try {
        val key = keyStore.getKey(keyAlias, null)
        val mac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256)
        mac.init(key)
        val signature = mac.doFinal(canonical.toByteArray(Charsets.UTF_8))
        Result.Success(signature.toHex())
    } catch (e: Exception) {
        Result.Error("Failed to sign canonical string", cause = e)
    }

    override suspend fun verify(canonical: String, hash: String): Result<Unit> = try {
        val key = keyStore.getKey(keyAlias, null)
        val mac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256)
        mac.init(key)
        val computed = mac.doFinal(canonical.toByteArray(Charsets.UTF_8)).toHex()
        if (computed == hash) {
            Result.Success(Unit)
        } else {
            Result.Error("Integrity verification failed: hash mismatch")
        }
    } catch (e: Exception) {
        Result.Error("Failed to verify signature", cause = e)
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
