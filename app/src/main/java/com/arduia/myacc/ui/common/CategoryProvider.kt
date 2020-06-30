package com.arduia.myacc.ui.common

import com.arduia.myacc.R
import com.arduia.myacc.ui.vto.CostCategory

class CategoryProvider{

    fun getDrawableCategory(category: CostCategory)
    = when(category){
        CostCategory.CLOTHES         -> R.drawable.ic_clothes
        CostCategory.HOUSEHOLD       -> R.drawable.ic_household
        CostCategory.TRANSPORTATION  -> R.drawable.ic_transportation
        CostCategory.FOOD            -> R.drawable.ic_food
        CostCategory.UTILITIES       -> R.drawable.ic_utities
        CostCategory.HEARTHCARE      -> R.drawable.ic_healthcare
        CostCategory.SOCIAL          -> R.drawable.ic_social
        CostCategory.EDUCATION       -> R.drawable.ic_education
        CostCategory.DONATIONS       -> R.drawable.ic_donations
        CostCategory.ENTERTAINMENT   -> R.drawable.ic_entertainment
        CostCategory.INCOME          -> R.drawable.ic_borrow
    }

}