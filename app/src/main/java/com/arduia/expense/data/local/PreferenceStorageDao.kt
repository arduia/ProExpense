package com.arduia.expense.data.local

import com.arduia.expense.model.Result
import kotlinx.coroutines.flow.Flow

interface PreferenceStorageDao {

    fun getSelectedLanguage(): Flow<String>

    suspend fun getSelectedLanguageSync(): String

    suspend fun setSelectedLanguage(id: String)

    fun getFirstUser(): Flow<Boolean>

    suspend fun getFirstUserSync(): Boolean

    suspend fun setFirstUser(isFirstUser: Boolean)

    fun getSelectedCurrencyNumber(): Flow<String>

    suspend fun getSelectedCurrencyNumberSync(): String

    suspend fun setSelectedCurrencyNumber(num: String)

    suspend fun setSelectedThemeMode(mode: Int)

    suspend fun getSelectedThemeModeSync(): Int

}