package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.storage.KeyRotationStore
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatabaseKeyManagerTest {
    private val keyDeriver = TestPinKeyDeriver()
    private val store = FakeKeyRotationStore()
    private val manager = DatabaseKeyManager(store, keyDeriver)

    @Test
    fun rotateToPinDerivedKey_setsPinProtectedKey() = runTest {
        val result = manager.rotateToPinDerivedKey("123456")
        assertTrue(result is Result.Success)
        assertTrue(store.pinProtected)
        assertTrue(manager.verifyDerivedKey("123456"))
    }

    @Test
    fun rotateToPinDerivedKey_rejectsInvalidPin() = runTest {
        val result = manager.rotateToPinDerivedKey("12345")
        assertTrue(result is Result.Error)
    }

    @Test
    fun rotateToRandomKey_clearsPinProtection() = runTest {
        manager.rotateToPinDerivedKey("123456")
        val result = manager.rotateToRandomKey()
        assertTrue(result is Result.Success)
        assertEquals(false, store.pinProtected)
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
