package com.arduia.expense.ui.vto

import androidx.annotation.DrawableRes
import com.arduia.expense.R

data class ExpenseVto(val id: Int,
                      val name:String,
                      val date:String,
                      @DrawableRes
                      val category: Int,
                      val amount:String,
                      val finance:String)

enum class ExpenseCategory(val categoryName:Int, val imageResource:Int){

    HOUSEHOLD       (R.string.type_house, R.drawable.ic_household),
    TRANSPORTATION  (R.string.type_transportation, R.drawable.ic_transportation),
    FOOD            (R.string.type_food, R.drawable.ic_food),
    UTILITIES       (R.string.type_utility, R.drawable.ic_utities),
    CLOTHES         (R.string.type_clothes, R.drawable.ic_clothes),
    HEARTH_CARE     (R.string.type_hearth_care, R.drawable.ic_healthcare),
    SOCIAL          (R.string.type_social, R.drawable.ic_social),
    EDUCATION       (R.string.type_education, R.drawable.ic_education),
    DONATIONS       (R.string.type_donation, R.drawable.ic_donations),
    ENTERTAINMENT   (R.string.type_entertainment, R.drawable.ic_entertainment),
    INCOME          (R.string.type_income, R.drawable.ic_borrow);

    companion object{

        fun getDrawableByName(name:String?) =
           getCategoryByName(name).imageResource

        fun getCategoryByName(name:String?) =
            when(name){
                INCOME.name         -> INCOME
                HOUSEHOLD.name      -> HOUSEHOLD
                TRANSPORTATION.name -> TRANSPORTATION
                FOOD.name           -> FOOD
                UTILITIES.name      -> UTILITIES
                CLOTHES.name        -> CLOTHES
                HEARTH_CARE.name    -> HEARTH_CARE
                SOCIAL.name         -> SOCIAL
                EDUCATION.name      -> EDUCATION
                DONATIONS.name      -> DONATIONS
                ENTERTAINMENT.name  -> ENTERTAINMENT
                else -> INCOME
            }

    }
}

