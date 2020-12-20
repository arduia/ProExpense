package com.arduia.expense.data.local

import kotlinx.coroutines.flow.Flow

interface CurrencyDao {

    fun getCurrencies(): Flow<List<CurrencyDto>>

}