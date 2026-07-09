package com.arduia.expense.domain

import kotlin.math.roundToLong

data class Money(
    val amount: Amount,
    val currency: CurrencyCode,
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) {
            "Cannot add Money in different currencies: $currency vs ${other.currency}"
        }
        return Money(amount + other.amount, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) {
            "Cannot subtract Money in different currencies: $currency vs ${other.currency}"
        }
        return Money(amount - other.amount, currency)
    }

    /** Manually-entered exchange rate conversion (US-LOG multi-currency) — no live-rate lookup. */
    fun convertedTo(
        currency: CurrencyCode,
        exchangeRate: Double,
    ): Money {
        require(exchangeRate > 0) { "Exchange rate must be positive" }
        return Money(Amount((amount.valueInCents * exchangeRate).roundToLong()), currency)
    }

    companion object {
        fun zero(currency: CurrencyCode) = Money(Amount.ZERO, currency)
    }
}
