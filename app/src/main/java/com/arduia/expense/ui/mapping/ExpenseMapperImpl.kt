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

class ExpenseMapperImpl(
     private val categoryProvider: ExpenseCategoryProvider,
     private val dateFormatter: DateFormat ,
     private val currencyFormatter: DecimalFormat
 ): ExpenseMapper{

     override fun mapToVto(expenseEnt: ExpenseEnt) =
         ExpenseVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name?:"",
             date = dateFormatter.format(expenseEnt.createdDate),
             amount = expenseEnt.amount.formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category)
         )

    override fun mapToDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseDetailsVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name?: "",
             date = dateFormatter.format(expenseEnt.createdDate),
             amount = expenseEnt.amount.formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category),
             note = expenseEnt.note?:""
         )

    override fun mapToUpdateDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseUpdateDataVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name ?: "",
             date = expenseEnt.createdDate,
             amount = expenseEnt.amount.toString(),
             category = categoryProvider.getCategoryByID(expenseEnt.category) ,
             note = expenseEnt.note ?: ""
         )

     private fun Long.formatCostValue()
         = currencyFormatter.format(this)

 }
