package com.arduia.expense.ui.common

import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.arduia.expense.R
import java.lang.Exception

class ExpenseCategoryProviderImpl(private val resource: Resources):
        ExpenseCategoryProvider{

    companion object{
        private val categoryList = mutableListOf<ExpenseCategory>()
    }

    override fun getCategoryList(): List<ExpenseCategory> {
        return categoryList
    }

    override fun getCategoryDrawableByID(id: Int): Int {
        return getCategoryByID(id).img
    }

    fun init(){
        if(categoryList.isEmpty()){
            categoryList.addAll(getCategoryData())
        }
    }

    private fun getCategoryData() = mutableListOf<ExpenseCategory>().apply {
        add(ExpenseCategory(1, (R.string.tmp_outcome).res(), R.drawable.ic_outcome ))
        add(ExpenseCategory(2, (R.string.tmp_income).res(), R.drawable.ic_income))
        add(ExpenseCategory(3, (R.string.tmp_food).res(), R.drawable.ic_food))
        add(ExpenseCategory(4, (R.string.tmp_household).res(), R.drawable.ic_household))
        add(ExpenseCategory(5, (R.string.tmp_social).res(), R.drawable.ic_social))
        add(ExpenseCategory(6, (R.string.tmp_entertainment).res(), R.drawable.ic_entertainment))
        add(ExpenseCategory(7, (R.string.tmp_transportation).res(), R.drawable.ic_transportation))
        add(ExpenseCategory( 8, (R.string.tmp_clothes).res(), R.drawable.ic_clothes))
        add(ExpenseCategory(9, (R.string.tmp_heath_care).res(), R.drawable.ic_healthcare))
        add(ExpenseCategory(10, (R.string.tmp_education).res(), R.drawable.ic_education))
        add(ExpenseCategory( 11, (R.string.tmp_donation).res(), R.drawable.ic_donations))
    }

    override fun getCategoryByID(id: Int): ExpenseCategory {
        return categoryList.firstOrNull{ it.id == id }?: throw Exception("Category Not Found $id")
    }

    override fun getIndexByCategory(category: ExpenseCategory): Int {
        return categoryList.indexOf(category)
    }

    private fun Int.res(): String = resource.getString(this)
}

data class ExpenseCategory(val id: Int,
                           val name: String,
                           @DrawableRes val img: Int)
