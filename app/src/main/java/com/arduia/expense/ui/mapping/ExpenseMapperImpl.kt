package com.arduia.expense.ui.mapping

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.entry.ExpenseUpdateDataVto
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseMapperImpl(
     private val categoryProvider: ExpenseCategoryProvider,
     private val dateFormatter: DateFormatter ,
     private val formatter: NumberFormat
 ): ExpenseMapper{
    private val currencyFormatter: NumberFormat = (NumberFormat.getInstance(Locale.ENGLISH) as DecimalFormat).apply {
        this.applyPattern("#.##")
    }
     override fun mapToVto(expenseEnt: ExpenseEnt) =
         ExpenseVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name?:"",
             date = dateFormatter.format(expenseEnt.modifiedDate),
             amount = expenseEnt.amount.getActual().formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category),
             currencySymbol = ""
         )

    override fun mapToDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseDetailsVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name?: "",
             date = dateFormatter.format(expenseEnt.modifiedDate),
             amount = expenseEnt.amount.getActual().formatCostValue(),
             finance = "",
             category = categoryProvider.getCategoryDrawableByID(expenseEnt.category),
             note = expenseEnt.note?:"",
             symbol =""
         )

    override fun mapToUpdateDetailVto(expenseEnt: ExpenseEnt) =
         ExpenseUpdateDataVto(
             id = expenseEnt.expenseId,
             name = expenseEnt.name ?: "",
             date = expenseEnt.createdDate,
             amount = expenseEnt.amount.getActual().toString(),
             category = categoryProvider.getCategoryByID(expenseEnt.category) ,
             note = expenseEnt.note ?: ""
         )

     private fun Float.formatCostValue()
         = currencyFormatter.format(this)

 }
