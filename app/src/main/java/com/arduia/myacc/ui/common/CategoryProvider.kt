package com.arduia.myacc.ui.common

import com.arduia.myacc.R
import com.arduia.myacc.ui.vto.ExpenseCategory

class CategoryProvider{

    fun getDrawableCategory(category: ExpenseCategory)
    = when(category){
        ExpenseCategory.CLOTHES         -> R.drawable.ic_clothes
        ExpenseCategory.HOUSEHOLD       -> R.drawable.ic_household
        ExpenseCategory.TRANSPORTATION  -> R.drawable.ic_transportation
        ExpenseCategory.FOOD            -> R.drawable.ic_food
        ExpenseCategory.UTILITIES       -> R.drawable.ic_utities
        ExpenseCategory.HEARTHCARE      -> R.drawable.ic_healthcare
        ExpenseCategory.SOCIAL          -> R.drawable.ic_social
        ExpenseCategory.EDUCATION       -> R.drawable.ic_education
        ExpenseCategory.DONATIONS       -> R.drawable.ic_donations
        ExpenseCategory.ENTERTAINMENT   -> R.drawable.ic_entertainment
        ExpenseCategory.INCOME          -> R.drawable.ic_borrow
    }

}