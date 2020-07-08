package com.arduia.expense.ui.common

import androidx.annotation.DrawableRes

interface ExpenseCategoryProvider{

    fun getCategoryList(): List<ExpenseCategory>

    @DrawableRes
    fun getCategoryDrawableByID(id: Int):Int

    fun getCategoryByID(id: Int):ExpenseCategory
}