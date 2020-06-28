package com.arduia.myacc.ui.mapping

import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.CostVto
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

 class AccountingMapper {

     private val dateFormatter = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
     private val currencyFormatter = DecimalFormat("###,###.#")

     fun mapToCostVto(transaction: Transaction) =
         CostVto(
             name = transaction.name?:"",
             date = dateFormatter.format(transaction.created_date),
             cost = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             cateogry = when(transaction.category){
                 else -> CostCategory.INCOME
             }
         )

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
