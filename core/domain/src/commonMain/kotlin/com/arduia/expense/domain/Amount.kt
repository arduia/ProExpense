package com.arduia.expense.domain

import kotlin.jvm.JvmInline
import kotlin.math.roundToLong

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

        /**
         * Parses a plain decimal user-input string (e.g. "12.50") into whole cents. Returns null for
         * blank, non-numeric, negative, or out-of-range input — callers decide how to surface that as
         * a validation error, this stays a pure parser with no platform/locale dependency.
         */
        fun parseOrNull(raw: String): Amount? {
            val value = raw.trim().toDoubleOrNull() ?: return null
            if (value < 0) return null
            val cents = (value * 100).roundToLong()
            return runCatching { Amount(cents) }.getOrNull()
        }
    }
}
