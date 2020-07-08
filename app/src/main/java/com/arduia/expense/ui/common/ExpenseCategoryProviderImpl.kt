package com.arduia.expense.ui.common

import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.arduia.expense.R
import java.lang.Exception

class ExpenseCategoryProviderImpl(private val resource: Resources):
        ExpenseCategoryProvider{

    private val categoryList = mutableListOf<ExpenseCategory>()

    override fun getCategoryList(): List<ExpenseCategory> {
        return categoryList
    }

    override fun getCategoryDrawableByID(id: Int): Int {
        return getCategoryByID(id).img
    }

    init {
        categoryList.clear()
        categoryList.addAll(getCategoryData())
    }

    private fun getCategoryData() = mutableListOf<ExpenseCategory>().apply {
        add(ExpenseCategory(1, (R.string.tmp_outcome).res(), R.drawable.ic_edit ))
    }

    override fun getCategoryByID(id: Int): ExpenseCategory {
        return categoryList.firstOrNull{ it.id == id }?: throw Exception("Category Not Found $id")
    }

    private fun Int.res(): String = resource.getString(this)

}

data class ExpenseCategory(val id: Int,
                           val name: String,
                           @DrawableRes val img: Int)