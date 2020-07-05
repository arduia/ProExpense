package com.arduia.expense.ui.mapping

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.CategoryProvider
import com.arduia.expense.ui.entry.ExpenseUpdateDataVto
import com.arduia.expense.ui.vto.ExpenseCategory
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpensePointVto
import com.arduia.expense.ui.vto.ExpenseVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

 class ExpenseMapper(
     private val categoryProvider: CategoryProvider,
     private val dateFormatter: DateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault()),
     private val currencyFormatter: DecimalFormat= DecimalFormat("###,###.#")
 ){

     fun mapToTransactionVto(expenseEnt: ExpenseEnt) =
         ExpenseVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name?:"",
             date = dateFormatter.format(expenseEnt.created_date),
             amount = expenseEnt.value.formatCostValue(),
             finance = expenseEnt.finance_type,
             category = when(expenseEnt.category){
                 else -> categoryProvider.getDrawableCategory(ExpenseCategory.FOOD)
             }
         )

     fun mapToTransactionDetail(transaction: ExpenseEnt) =
         ExpenseDetailsVto(
             id = transaction.expense_id,
             name = transaction.name?: "",
             date = dateFormatter.format(transaction.created_date),
             amount = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             category = when(transaction.category){
                 else -> categoryProvider.getDrawableCategory(ExpenseCategory.FOOD)
             },
             note = transaction.note?:""
         )

     fun mapToUpdateDetail(expenseEnt: ExpenseEnt) =
         ExpenseUpdateDataVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name ?: "",
             date = expenseEnt.created_date,
             amount = expenseEnt.value.toString(),
             category = when(expenseEnt.category){
                 else -> ExpenseCategory.FOOD
             },
             note = expenseEnt.note ?: ""
         )


     fun mapToGraphData(rawData: Map<Int, Int>) =
         ExpensePointVto().apply {
             this.rates = rawData
         }

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
