package com.arduia.expense.ui.common.category

import androidx.annotation.DrawableRes

interface ExpenseCategoryProvider{

    fun getCategoryList(): List<ExpenseCategory>

    @DrawableRes
    fun getCategoryDrawableByID(id: Int):Int

    fun getCategoryByID(id: Int): ExpenseCategory

    fun getIndexByCategory(category: ExpenseCategory):Int
}
