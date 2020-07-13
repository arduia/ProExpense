package com.arduia.expense.ui.vto

import androidx.annotation.DrawableRes

data class ExpenseDetailsVto(val id: Int,
                             val name: String,
                             val date: String,
                             @DrawableRes
                             val category: Int,
                             val amount: String,
                             val finance: String,
                             val note: String)
