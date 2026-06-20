package com.arduia.expense.feature.sharedcost

import com.arduia.expense.domain.Amount

enum class SplitMode {
    EVEN,
    CUSTOM,
}

fun calculateEvenSplit(totalCents: Long, peopleCount: Int): List<Long> {
    require(peopleCount > 0) { "peopleCount must be positive" }
    require(totalCents >= 0) { "total must be non-negative" }
    val base = totalCents / peopleCount
    val remainder = totalCents % peopleCount
    return List(peopleCount) { index ->
        base + if (index < remainder) 1L else 0L
    }
}

fun validateCustomSplit(totalCents: Long, customShares: List<Long>): String? {
    val sum = customShares.sum()
    val diff = totalCents - sum
    return when {
        diff == 0L -> null
        diff > 0 -> "Amounts are ${formatCentsShort(diff)} short of the total"
        else -> "Amounts are ${formatCentsShort(-diff)} over the total"
    }
}

private fun formatCentsShort(cents: Long): String {
    val whole = cents / 100
    val fraction = cents % 100
    return if (fraction == 0L) "$$whole" else "$$whole.${fraction.toString().padStart(2, '0')}"
}
