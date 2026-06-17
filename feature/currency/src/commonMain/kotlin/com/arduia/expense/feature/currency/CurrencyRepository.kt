package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode

data class CurrencySettings(
    val homeCurrency: CurrencyCode,
)

data class ExchangeRateInput(
    val from: CurrencyCode,
    val to: CurrencyCode,
    val rate: Double,
)

interface CurrencyRepository {
    suspend fun getSettings(): Result<CurrencySettings>

    suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit>
}

fun convertToHomeCurrency(amount: Amount, exchangeRate: Double): Amount {
    require(exchangeRate > 0) { "Exchange rate must be positive" }
    return Amount((amount.valueInCents * exchangeRate).toLong())
}
