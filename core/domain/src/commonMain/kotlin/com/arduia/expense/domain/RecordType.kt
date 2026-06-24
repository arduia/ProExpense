package com.arduia.expense.domain

enum class RecordType {
    EXPENSE,
    INCOME,
}

/** Stable codes independent of enum declaration order (never use .ordinal for storage). */
fun RecordType.toCode(): Long = when (this) {
    RecordType.EXPENSE -> 0L
    RecordType.INCOME -> 1L
}

fun Long.toRecordType(): RecordType = when (this) {
    0L -> RecordType.EXPENSE
    1L -> RecordType.INCOME
    else -> error("Unknown RecordType code: $this")
}
