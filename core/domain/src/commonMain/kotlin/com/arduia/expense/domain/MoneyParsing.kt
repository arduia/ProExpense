package com.arduia.expense.domain

private val AMOUNT_PATTERN = Regex("""^\d*\.?\d*$""")

/**
 * Parses a user-entered decimal amount into integer cents without floating-point rounding
 * error (e.g. "4.35" → 435, which `(4.35 * 100).toLong()` mis-rounds to 434). Returns null
 * for malformed input. Fraction is truncated to two places to match the keypad cap.
 */
fun parseAmountToCents(text: String): Long? {
    val trimmed = text.trim()
    if (trimmed.isEmpty() || !AMOUNT_PATTERN.matches(trimmed)) return null
    val parts = trimmed.split('.')
    val whole = parts[0].ifEmpty { "0" }.toLongOrNull() ?: return null
    val fraction = parts.getOrNull(1).orEmpty().padEnd(2, '0').take(2).toLongOrNull() ?: 0L
    return whole * 100 + fraction
}
