package com.arduia.expense.domain

fun currencySymbolFor(code: String): String = when (code) {
    "USD" -> "$"
    "EUR" -> "€"
    "GBP" -> "£"
    "JPY" -> "¥"
    "INR" -> "₹"
    "AED" -> "د.إ"
    else -> code
}

fun Amount.formatWithSymbol(currencyCode: String): String {
    val symbol = currencySymbolFor(currencyCode)
    val whole = valueInCents / 100
    val fraction = kotlin.math.abs(valueInCents % 100)
    val wholeFormatted = whole.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return if (fraction == 0L) {
        "$symbol$wholeFormatted"
    } else {
        "$symbol$wholeFormatted.${fraction.toString().padStart(2, '0')}"
    }
}
