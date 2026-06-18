package com.arduia.expense.storage

interface LocalDataStore {
    suspend fun isAvailable(): Boolean
}
