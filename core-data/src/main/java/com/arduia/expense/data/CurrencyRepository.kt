package com.arduia.expense.data

import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.FlowResult
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun getCurrencies(): FlowResult<List<CurrencyDto>>

    fun getSelectedCacheCurrency(): FlowResult<CurrencyDto>

    suspend fun setSelectedCacheCurrency(num: String)

}