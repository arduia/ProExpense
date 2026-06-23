package com.arduia.expense.domain

data class Money(val amount: Amount, val currency: CurrencyCode) {
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

    companion object {
        fun zero(currency: CurrencyCode) = Money(Amount.ZERO, currency)
    }
}
