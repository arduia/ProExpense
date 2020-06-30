package com.arduia.myacc.ui.mapping

import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.ui.common.CategoryProvider
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.TransactionVto
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

 class TransactionMapper( private val categoryProvider: CategoryProvider ){

     private val dateFormatter = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
     private val currencyFormatter = DecimalFormat("###,###.#")

     fun mapToCostVto(transaction: Transaction) =
         TransactionVto(
             id = transaction.transaction_id,
             name = transaction.name?:"",
             date = dateFormatter.format(transaction.created_date),
             cost = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             category = when(transaction.category){
                 else -> categoryProvider.getDrawableCategory(CostCategory.FOOD)
             }
         )

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
