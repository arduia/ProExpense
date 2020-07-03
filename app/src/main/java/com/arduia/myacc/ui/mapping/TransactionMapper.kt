package com.arduia.myacc.ui.mapping

import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.ui.common.CategoryProvider
import com.arduia.myacc.ui.entry.UpdateDataVto
import com.arduia.myacc.ui.vto.CostCategory
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.arduia.myacc.ui.vto.TransactionVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

 class TransactionMapper(
     private val categoryProvider: CategoryProvider,
     private val dateFormatter: DateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault()),
     private val currencyFormatter: DecimalFormat= DecimalFormat("###,###.#")
 ){

     fun mapToTransactionVto(transaction: Transaction) =
         TransactionVto(
             id = transaction.transaction_id,
             name = transaction.name?:"",
             date = dateFormatter.format(transaction.created_date),
             amount = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             category = when(transaction.category){
                 else -> categoryProvider.getDrawableCategory(CostCategory.FOOD)
             }
         )

     fun mapToTransactionDetail(transaction: Transaction) =
         TransactionDetailsVto(
             id = transaction.transaction_id,
             name = transaction.name?: "",
             date = dateFormatter.format(transaction.created_date),
             amount = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             category = when(transaction.category){
                 else -> categoryProvider.getDrawableCategory(CostCategory.FOOD)
             },
             note = transaction.note?:""
         )

     fun mapToUpdateDetail(transaction: Transaction) =
         UpdateDataVto(
             id = transaction.transaction_id,
             name = transaction.name ?: "",
             date = transaction.created_date,
             amount = transaction.value.toString(),
             category = when(transaction.category){
                 else -> CostCategory.FOOD
             },
             note = transaction.note ?: ""
         )

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
