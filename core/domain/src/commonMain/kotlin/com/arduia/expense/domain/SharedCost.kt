package com.arduia.expense.domain

data class SharedCost(
    val id: String,
    val title: String,
    val totalAmount: Amount,
    val currency: CurrencyCode,
    val participants: List<Participant>,
    val recordedAtEpochMillis: Long,
)
