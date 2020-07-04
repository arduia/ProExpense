package com.arduia.myacc.ui.vto

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class ExpenseVto(val id: Int,
                      val name:String,
                      val date:String,
                      @DrawableRes
                          val category: Int,
                      val amount:String,
                      val finance:String)

enum class ExpenseCategory{
    HOUSEHOLD,
    TRANSPORTATION,
    FOOD,
    UTILITIES,
    CLOTHES,
    HEARTHCARE,
    SOCIAL,
    EDUCATION,
    DONATIONS,
    ENTERTAINMENT,
    INCOME
}

