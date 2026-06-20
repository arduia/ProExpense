/**
 * Domain rules (design plan C6/A2):
 * - PIN must be exactly 6 digits for key derivation.
 * - rotateToPinDerivedKey stores PIN-protected key; verifyDerivedKey checks match.
 * - rotateToRandomKey clears PIN protection (disable PIN path).
 * - DatabaseRekeyer receives new passphrase on rotation when configured.
 */
package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.storage.DatabaseRekeyer
import com.arduia.expense.storage.KeyRotationStore
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatabaseKeyManagerTest {
    private val keyDeriver = TestPinKeyDeriver()
    private val store = FakeKeyRotationStore()
    private val rekeyer = RecordingRekeyer()
    private val manager = DatabaseKeyManager(store, keyDeriver, rekeyer)

    @Test
    fun rotateToPinDerivedKey_setsPinProtectedKey() = runTest {
        val result = manager.rotateToPinDerivedKey("123456")
        assertTrue(result is Result.Success)
        assertTrue(store.pinProtected)
        assertTrue(manager.verifyDerivedKey("123456"))
        assertNotNull(rekeyer.lastKey)
    }

    @Test
    fun rotateToPinDerivedKey_rejectsInvalidPin() = runTest {
        val result = manager.rotateToPinDerivedKey("12345")
        assertTrue(result is Result.Error)
        assertEquals(null, rekeyer.lastKey)
    }

    @Test
    fun rotateToPinDerivedKey_rejectsNonDigitPin() = runTest {
        val result = manager.rotateToPinDerivedKey("12345a")
        assertTrue(result is Result.Error)
    }

    @Test
    fun rotateToRandomKey_clearsPinProtection() = runTest {
        manager.rotateToPinDerivedKey("123456")
        val result = manager.rotateToRandomKey()
        assertTrue(result is Result.Success)
        assertEquals(false, store.pinProtected)
        assertNotNull(rekeyer.lastKey)
    }
}

private class RecordingRekeyer : DatabaseRekeyer {
    var lastKey: ByteArray? = null

    override suspend fun rekey(newPassphrase: ByteArray) {
        lastKey = newPassphrase.copyOf()
    }
}

private class TestPinKeyDeriver : PinKeyDeriver {
    override fun deriveKey(pin: String, salt: ByteArray): ByteArray =
        (pin + salt.decodeToString()).encodeToByteArray().copyOf(32)

    override fun generateSalt(length: Int): ByteArray = ByteArray(length) { 1 }
}

private class FakeKeyRotationStore : KeyRotationStore {
    var pinProtected = false
    private var activeKey: ByteArray? = null
    private var pinSalt: ByteArray? = null

    override suspend fun getActiveKey(): ByteArray? = activeKey?.copyOf()

    override suspend fun getPinSalt(): ByteArray? = pinSalt?.copyOf()

    override suspend fun setPinDerivedKey(key: ByteArray, salt: ByteArray) {
        activeKey = key.copyOf()
        pinSalt = salt.copyOf()
        pinProtected = true
    }

    override suspend fun setRandomKey(key: ByteArray) {
        activeKey = key.copyOf()
        pinSalt = null
        pinProtected = false
    }

    override suspend fun isPinProtected(): Boolean = pinProtected
}
