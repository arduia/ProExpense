package com.arduia.expense.data

import android.content.Context
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import kotlinx.coroutines.CoroutineScope

interface SettingsRepository{

    fun getSelectedLanguage(): FlowResult<String>

    suspend fun setSelectedLanguage(id: String)

    suspend fun getSelectedLanguageSync(): Result<String>

    fun getFirstUser(): FlowResult<Boolean>

    suspend fun getFirstUserSync():  Result<Boolean>

    suspend fun setFirstUser(isFirstUser: Boolean)

    fun getSelectedCurrencyNumber(): FlowResult<String>

    suspend fun getSelectedCurrencyNumberSync(): Result<String>

    suspend fun setSelectedCurrencyNumber(num: String)

    interface Factory{
        fun create(context: Context): SettingsRepository
    }
}