package com.arduia.expense.ui.mapping

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.entry.ExpenseUpdateDataVto
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpenseMapper(
     private val categoryProvider: ExpenseCategoryProvider,
     private val dateFormatter: DateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault()),
     private val currencyFormatter: DecimalFormat = DecimalFormat("#,###")
 ){

     fun mapToExpenseVto(expenseEnt: ExpenseEnt) =
         ExpenseVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name?:"",
             date = dateFormatter.format(expenseEnt.created_date),
             amount = expenseEnt.amount.formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category)
         )

     fun mapToExpenseDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseDetailsVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name?: "",
             date = dateFormatter.format(expenseEnt.created_date),
             amount = expenseEnt.amount.formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category),
             note = expenseEnt.note?:""
         )

     fun mapToUpdateDetail(expenseEnt: ExpenseEnt) =
         ExpenseUpdateDataVto(
             id = expenseEnt.expense_id,
             name = expenseEnt.name ?: "",
             date = expenseEnt.created_date,
             amount = expenseEnt.amount.toString(),
             category = categoryProvider.getCategoryByID(expenseEnt.category) ,
             note = expenseEnt.note ?: ""
         )

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
