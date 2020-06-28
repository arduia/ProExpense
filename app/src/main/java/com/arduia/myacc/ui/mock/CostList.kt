package com.arduia.myacc.ui.mock

import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.TransactionVto

fun costList() =
    mutableListOf<TransactionVto>().apply {
    add(TransactionVto("Salary", "1/9/2020", CostCategory.INCOME,"+1,000,000", finance = "KBZ"))
    add(TransactionVto("Sugar", "3/3/2019", CostCategory.HOUSEHOLD,"-10000", finance = "CASH"))
    add(TransactionVto("Cola", "3/3/2019", CostCategory.FOOD,"-50,000", finance = "CASH"))
    add(TransactionVto("Coffee", "1/9/2020", CostCategory.FOOD,"-1,000", finance = "CASH"))
    add(TransactionVto("MPT Bill", "9/6/2020", CostCategory.HOUSEHOLD,"-90,000", finance = "CASH"))
    }
