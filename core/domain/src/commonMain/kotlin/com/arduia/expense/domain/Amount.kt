package com.arduia.expense.domain

data class Amount(val valueInCents: Long) {
    init {
        require(valueInCents in 0..MAX_VALUE_IN_CENTS) {
            "Amount must be between 0 and $MAX_VALUE_IN_CENTS cents"
        }
    }

    companion object {
        const val MAX_VALUE_IN_CENTS = 99_999_999_999L
    }
}
