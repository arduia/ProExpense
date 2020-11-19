package com.arduia.expense.data

import com.arduia.expense.model.FlowResult
import kotlinx.coroutines.flow.Flow

interface SettingsRepository{

    fun getSelectedLanguage(): FlowResult<String>

    suspend fun setSelectedLanguage(id: String)

    fun getFirstUser(): FlowResult<Boolean>

    suspend fun setFirstUser(isFirstUser: Boolean)

    fun getSelectedCurrencyNumber(): FlowResult<String>

    suspend fun setSelectedCurrencyNumber(num: String)
}