package com.arduia.expense.ui.mapping

import com.arduia.expense.data.local.ExpenseEnt
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
     private val dateFormatter: DateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault()),
     private val currencyFormatter: DecimalFormat= DecimalFormat("###,###.#")
 ){

     fun mapToExpenseVto(expenseEnt: ExpenseEnt) =
         ExpenseVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name?: "",
             date = dateFormatter.format(expenseEnt.created_date),
             amount = expenseEnt.value.formatCostValue(),
             finance = expenseEnt.finance_type,
             category = ExpenseCategory.getDrawableByName(expenseEnt.category)
         )

     fun mapToExpenseDetailVto(transaction: ExpenseEnt) =
         ExpenseDetailsVto(
             id = transaction.expense_id,
             name = transaction.name?: "",
             date = dateFormatter.format(transaction.created_date),
             amount = transaction.value.formatCostValue(),
             finance = transaction.finance_type,
             category = ExpenseCategory.getDrawableByName(transaction.category),
             note = transaction.note?: ""
         )

     fun mapToUpdateDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseUpdateDataVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name ?: "",
             date = expenseEnt.created_date,
             amount = expenseEnt.value.toString(),
             category = ExpenseCategory.getCategoryByName(expenseEnt.category),
             note = expenseEnt.note ?: ""
         )


     fun mapToGraphPointVto(rawData: Map<Int, Int>) =
         ExpensePointVto().apply {
             this.rates =  rawData
         }

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
