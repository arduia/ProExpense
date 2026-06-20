package com.arduia.expense.feature.logging

import com.arduia.expense.domain.parseAmountToCents

data class LogCategory(
    val id: String,
    val label: String,
    val accentArgb: Long,
    val tintArgb: Long,
    val isCustom: Boolean = false,
)

val defaultLogCategories = listOf(
    LogCategory("food", "Food", 0xFF039BE5, 0xFFE1F5FE),
    LogCategory("transport", "Transport", 0xFF0288D1, 0xFFB3E5FC),
    LogCategory("shopping", "Shopping", 0xFFEF5350, 0xFFFFCDD2),
    LogCategory("bills", "Bills", 0xFF757575, 0xFFEEEEEE),
    LogCategory("health", "Health", 0xFF4CAF50, 0xFFC8E6C9),
    LogCategory("entertainment", "Entertainment", 0xFF0277BD, 0xFF81D4FA),
)

val customLogCategories = listOf(
    LogCategory("coffee", "Coffee runs", 0xFF9E9E9E, 0xFFE0E0E0, isCustom = true),
    LogCategory("pet", "Pet care", 0xFF66BB6A, 0xFFDCEDC8, isCustom = true),
)

val allLogCategories: List<LogCategory> = defaultLogCategories + customLogCategories

fun logCategoryById(id: String): LogCategory =
    allLogCategories.firstOrNull { it.id == id } ?: defaultLogCategories.first()

data class AmountDisplay(
    val whole: String,
    val decimal: String?,
    val isPlaceholder: Boolean,
)

object AmountInputLogic {
    private const val MAX_DIGITS_BEFORE_DECIMAL = 9
    private const val MAX_DECIMAL_PLACES = 2

    fun applyKey(current: String, key: String): String = when (key) {
        "⌫" -> if (current.isEmpty()) "" else current.dropLast(1)
        "." -> when {
            current.contains('.') -> current
            current.isEmpty() -> "0."
            else -> "$current."
        }
        else -> {
            if (current.contains('.')) {
                val decimals = current.substringAfter('.')
                if (decimals.length >= MAX_DECIMAL_PLACES) return current
            } else if (!current.contains('.') && current.length >= MAX_DIGITS_BEFORE_DECIMAL) {
                return current
            }
            if (current == "0" && key != ".") key else current + key
        }
    }

    fun toDisplay(amount: String): AmountDisplay {
        if (amount.isBlank()) {
            return AmountDisplay(whole = "0", decimal = null, isPlaceholder = true)
        }
        val hasDecimal = amount.contains('.')
        val parts = amount.split('.')
        val whole = (parts.firstOrNull().orEmpty().ifEmpty { "0" })
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
        val decimal = if (hasDecimal) parts.getOrNull(1).orEmpty() else null
        return AmountDisplay(whole = whole, decimal = decimal, isPlaceholder = false)
    }

    fun canProceed(amount: String): Boolean {
        val value = amount.toDoubleOrNull() ?: return false
        return value > 0.0
    }

    fun toCents(amount: String): Long? {
        if (!canProceed(amount)) return null
        return parseAmountToCents(amount)
    }
}
