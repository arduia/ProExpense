package com.arduia.expense.ui.design

/**
 * Single source of truth for amount keypad entry and money display formatting — pure Kotlin, no
 * Android/Compose dependency, so it's shared by every feature module today and by iOS for free
 * once an iOS target is added to this module (see shared/src/iosMain's readiness stubs).
 */
object AmountInput {
    private const val MAX_WHOLE_DIGITS = 7
    private const val MAX_FRACTION_DIGITS = 2

    fun applyKey(rawValue: String, key: String): String {
        if (key == ".") return applyDecimal(rawValue)
        if (key.length != 1 || !key[0].isDigit()) return rawValue
        return applyDigit(rawValue, key[0])
    }

    fun applyBackspace(rawValue: String): String {
        if (rawValue.isEmpty()) return rawValue
        return normalize(rawValue.dropLast(1))
    }

    fun formatDisplay(rawValue: String): String {
        if (rawValue.isEmpty()) return "0"
        val parts = rawValue.split('.')
        val whole = parts[0].ifEmpty { "0" }
        val groupedWhole = whole.reversed().chunked(3).joinToString(",").reversed()
        return if (parts.size > 1) {
            "$groupedWhole.${parts[1]}"
        } else {
            groupedWhole
        }
    }

    /**
     * Formats non-negative cents for read-only display (transaction totals, budget labels) —
     * drops the fraction when it's exactly zero ("22" not "22.00") instead of always padding to
     * 2 decimals. Callers with a possibly-negative value should format `abs(valueInCents)` and
     * prepend the sign themselves, matching the raw-input convention elsewhere in this object.
     */
    fun formatMoney(valueInCents: Long): String {
        val whole = valueInCents / 100
        val fractionCents = valueInCents % 100
        return if (fractionCents == 0L) {
            formatDisplay(whole.toString())
        } else {
            formatDisplay("$whole.${fractionCents.toString().padStart(2, '0')}")
        }
    }

    /** Convenience for the common case: [formatMoney] with the currency symbol prefixed. */
    fun formatMoney(valueInCents: Long, currencySymbol: String): String =
        currencySymbol + formatMoney(valueInCents)

    /**
     * Like [formatMoney] but tolerates a negative [valueInCents] — the minus sign is placed
     * before the currency symbol ("-$50") instead of requiring the caller to abs() and prepend
     * it manually.
     */
    fun formatMoneySigned(valueInCents: Long, currencySymbol: String): String {
        val sign = if (valueInCents < 0) "-" else ""
        return sign + formatMoney(if (valueInCents < 0) -valueInCents else valueInCents, currencySymbol)
    }

    fun numericValue(rawValue: String): Double? {
        if (rawValue.isEmpty()) return 0.0
        return rawValue.toDoubleOrNull()
    }

    fun canProceed(rawValue: String): Boolean = (numericValue(rawValue) ?: 0.0) > 0.0

    private fun applyDecimal(rawValue: String): String {
        if (rawValue.contains('.')) return rawValue
        return normalize(if (rawValue.isEmpty()) "0." else "$rawValue.")
    }

    private fun applyDigit(rawValue: String, digit: Char): String {
        val decimalIndex = rawValue.indexOf('.')
        if (decimalIndex < 0) {
            if (rawValue.length >= MAX_WHOLE_DIGITS) return rawValue
            return normalize(rawValue + digit)
        }
        val fraction = rawValue.substring(decimalIndex + 1)
        if (fraction.length >= MAX_FRACTION_DIGITS) return rawValue
        return normalize(rawValue + digit)
    }

    private fun normalize(value: String): String {
        if (value.isEmpty()) return value
        if (value == ".") return "0."
        if (value.startsWith('.')) return "0$value"
        if (!value.contains('.')) {
            val trimmed = value.trimStart('0').ifEmpty { "0" }
            return trimmed.take(MAX_WHOLE_DIGITS.coerceAtLeast(trimmed.length))
        }
        val whole = value.substringBefore('.').trimStart('0').ifEmpty { "0" }
        val fraction = value.substringAfter('.')
        return "$whole.$fraction"
    }
}
