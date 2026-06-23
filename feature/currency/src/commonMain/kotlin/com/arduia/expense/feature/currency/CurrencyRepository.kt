package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money

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

fun convertToHomeCurrency(money: Money, exchangeRate: Double, homeCurrency: CurrencyCode): Money {
    require(exchangeRate > 0) { "Exchange rate must be positive" }
    return Money(Amount((money.amount.valueInCents * exchangeRate).toLong()), homeCurrency)
}
