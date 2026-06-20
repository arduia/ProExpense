package com.arduia.expense.feature.logging

import com.arduia.expense.domain.ExpenseTagType

data class ExpenseTagOption(
    val type: ExpenseTagType,
    val id: String,
    val label: String,
)
