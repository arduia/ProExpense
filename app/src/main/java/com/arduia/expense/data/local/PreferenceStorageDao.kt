package com.arduia.expense.data.local

import kotlinx.coroutines.flow.Flow

interface PreferenceStorageDao {
    fun getSelectedLanguage(): Flow<String>

    fun setSelectedLanguage(id: String)

    fun getFirstUser(): Flow<Boolean>

    fun setFirstUser(isFirstUser: Boolean)

    fun getSelectedCurrencyNumber(): Flow<String>

    fun setSelectedCurrencyNumber(num: String)
}