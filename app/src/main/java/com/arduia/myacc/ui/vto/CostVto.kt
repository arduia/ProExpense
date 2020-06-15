package com.arduia.myacc.ui.vto

data class CostVto(val name:String,
                   val date:String,
                   val cateogry:CostCategory,
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

