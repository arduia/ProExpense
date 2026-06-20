package com.arduia.expense.ui.design

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
