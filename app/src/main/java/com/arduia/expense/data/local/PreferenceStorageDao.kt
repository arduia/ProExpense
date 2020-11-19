package com.arduia.expense.data.local

import kotlinx.coroutines.flow.Flow

interface PreferenceStorageDao {
    fun getSelectedLanguage(): Flow<String>

    suspend fun setSelectedLanguage(id: String)

    fun getFirstUser(): Flow<Boolean>

    suspend fun setFirstUser(isFirstUser: Boolean)

    fun getSelectedCurrencyNumber(): Flow<String>

    suspend fun setSelectedCurrencyNumber(num: String)
}