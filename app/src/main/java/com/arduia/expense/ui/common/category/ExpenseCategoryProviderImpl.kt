package com.arduia.expense.ui.common.category

import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.arduia.expense.R
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.CLOTHES
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.DONATION
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.EDUCATION
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.ENTERTAINMENT
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.FOOD
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.HEALTH_CARE
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.HOUSING
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.INCOME
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.OTHERS
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.SOCIAL
import com.arduia.expense.ui.common.category.ExpenseCategory.Companion.TRANSPORTATION
import java.lang.Exception

class ExpenseCategoryProviderImpl(private val resource: Resources) :
    ExpenseCategoryProvider {


    private val categoryList = mutableListOf<ExpenseCategory>()

    override fun getCategoryList(): List<ExpenseCategory> {
        return categoryList
    }

    override fun getCategoryDrawableByID(id: Int): Int {
        return getCategoryByID(id).img
    }

    init {
        categoryList.addAll(getCategoryData())
    }

    private fun getCategoryData() = mutableListOf<ExpenseCategory>().apply {
        add(ExpenseCategory(INCOME, (R.string.income).res(), R.drawable.ic_income))
        add(ExpenseCategory(FOOD, (R.string.food).res(), R.drawable.ic_food))
        add(ExpenseCategory(HOUSING, (R.string.housing).res(), R.drawable.ic_household))
        add(ExpenseCategory(SOCIAL, (R.string.social).res(), R.drawable.ic_social))
        add(
            ExpenseCategory(
                ENTERTAINMENT,
                (R.string.entertainment).res(),
                R.drawable.ic_entertainment
            )
        )
        add(
            ExpenseCategory(
                TRANSPORTATION,
                (R.string.transportation).res(),
                R.drawable.ic_transportation
            )
        )
        add(ExpenseCategory(CLOTHES, (R.string.clothes).res(), R.drawable.ic_clothes))
        add(ExpenseCategory(HEALTH_CARE, (R.string.health_care).res(), R.drawable.ic_healthcare))
        add(ExpenseCategory(EDUCATION, (R.string.education).res(), R.drawable.ic_education))
        add(ExpenseCategory(DONATION, (R.string.donation).res(), R.drawable.ic_donation))
        add(ExpenseCategory(OTHERS, (R.string.others).res(), R.drawable.ic_outcome))
    }

    override fun getCategoryByID(id: Int): ExpenseCategory {
        return categoryList.firstOrNull { it.id == id } ?: throw Exception("Category Not Found $id")
    }

    override fun getIndexByCategory(category: ExpenseCategory): Int {
        return categoryList.indexOf(category)
    }

    private fun Int.res(): String = resource.getString(this)
}

data class ExpenseCategory(
    val id: Int,
    val name: String,
    @DrawableRes val img: Int
) {
    companion object {
        const val OTHERS = 1
        const val INCOME = 2
        const val FOOD = 3
        const val HOUSING = 4
        const val SOCIAL = 5
        const val ENTERTAINMENT = 6
        const val TRANSPORTATION = 7
        const val CLOTHES = 8
        const val HEALTH_CARE = 9
        const val EDUCATION = 10
        const val DONATION = 11
    }
}
