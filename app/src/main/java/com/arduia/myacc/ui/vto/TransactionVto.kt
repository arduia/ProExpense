package com.arduia.myacc.ui.vto

data class TransactionVto(val name:String,
                          val date:String,
                          val cateogry:CostCategory,
                          val cost:String,
                          val finance:String,
                          var isSelected:Boolean = false)

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

