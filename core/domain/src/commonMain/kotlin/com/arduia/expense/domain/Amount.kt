package com.arduia.expense.domain

@JvmInline
value class Amount(val valueInCents: Long) : Comparable<Amount> {
    init {
        require(valueInCents in 0..MAX_VALUE_IN_CENTS) {
            "Amount must be between 0 and $MAX_VALUE_IN_CENTS cents"
        }
    }

    operator fun plus(other: Amount): Amount = Amount(valueInCents + other.valueInCents)

    operator fun minus(other: Amount): Amount = Amount(valueInCents - other.valueInCents)

    override fun compareTo(other: Amount): Int = valueInCents.compareTo(other.valueInCents)

    companion object {
        const val MAX_VALUE_IN_CENTS = 99_999_999_999L
        val ZERO = Amount(0)
    }
}
