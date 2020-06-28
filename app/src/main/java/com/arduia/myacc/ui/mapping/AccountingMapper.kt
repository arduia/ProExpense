package com.arduia.myacc.ui.mapping

import android.annotation.SuppressLint
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto
import java.text.SimpleDateFormat
import java.util.*

 class AccountingMapper {

     private val costDateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())

     fun mapToCostVto(transaction: Transaction) =
         CostVto(
             name = transaction.name?:"",
             date = costDateFormat.format(transaction.created_date),
             cost = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             cateogry = when(transaction.category){
                 else -> CostCategory.INCOME
             }
         )

     private fun Long.formatCostValue(): String = this.toString()

 }
