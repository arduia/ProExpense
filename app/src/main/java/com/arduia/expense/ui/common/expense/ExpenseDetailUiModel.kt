package com.arduia.expense.ui.common.expense

import androidx.annotation.DrawableRes

data class ExpenseDetailUiModel(val id: Int,
                                val name: String,
                                val date: String,
                                @DrawableRes
                             val category: Int,
                                val symbol: String,
                                val amount: String,
                                val finance: String,
                                val note: String)
