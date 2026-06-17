package com.arduia.expense.domain

data class FinanceRecord(
    val id: String,
    val amount: Amount,
    val currency: CurrencyCode,
    val homeCurrencyAmount: Amount,
    val categoryId: String,
    val type: RecordType,
    val note: String?,
    val recordedAtEpochMillis: Long,
)
