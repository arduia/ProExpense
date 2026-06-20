package com.arduia.expense.storage

interface KeyRotationStore {
    suspend fun getActiveKey(): ByteArray?

    suspend fun getPinSalt(): ByteArray?

    suspend fun setPinDerivedKey(key: ByteArray, salt: ByteArray)

    suspend fun setRandomKey(key: ByteArray)

    suspend fun isPinProtected(): Boolean
}
