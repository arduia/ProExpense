package com.arduia.expense.feature.logging

data class LoggedExpenseHandoff(
    val categoryId: String,
    val note: String,
    val rawAmount: String,
    val timeLabel: String,
    val linkedTagLabel: String? = null,
)
