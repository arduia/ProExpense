package com.arduia.expense.ui.expenselogs

import androidx.annotation.DrawableRes

data class ExpenseUiModel(
    val id: Int,
    val name: String,
    val date: String,
    @DrawableRes
    val category: Int,
    val amount: String,
    val currencySymbol: String,
    val finance: String
)



