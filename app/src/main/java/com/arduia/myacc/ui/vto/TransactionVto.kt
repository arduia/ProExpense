package com.arduia.myacc.ui.vto

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class TransactionVto(val id: Int,
                          val name:String,
                          val date:String,
                          @DrawableRes
                          val category: Int,
                          val cost:String,
                          val finance:String)

enum class CostCategory{
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

