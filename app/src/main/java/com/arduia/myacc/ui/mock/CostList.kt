package com.arduia.myacc.ui.mock

import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto

fun costList() = mutableListOf<CostVto>().apply {
    add(CostVto("Salary","1/9/2020",CostCategory.INCOME,"+1,000,000",finance = "KBZ"))
    add(CostVto("Sugar","3/3/2019",CostCategory.HOUSEHOLD,"-10000",finance = "CASH"))
    add(CostVto("Cola","3/3/2019",CostCategory.FOOD,"-50,000",finance = "CASH"))
    add(CostVto("Coffee","1/9/2020",CostCategory.FOOD,"-1,000",finance = "CASH"))
    add(CostVto("MPT Bill","9/6/2020",CostCategory.HOUSEHOLD,"-90,000",finance = "CASH"))
}
