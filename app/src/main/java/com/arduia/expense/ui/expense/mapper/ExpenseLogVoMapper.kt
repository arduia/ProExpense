package com.arduia.expense.ui.expense.mapper

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.expense.ExpenseLogVo
import com.arduia.expense.ui.expense.swipe.SwipeItemState
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import com.arduia.expense.ui.vto.ExpenseVto
import timber.log.Timber
import java.text.DateFormat
import java.text.NumberFormat

class ExpenseLogVoMapper(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: DateFormat,
    private val currencyFormatter: NumberFormat
) : Mapper<ExpenseEnt, ExpenseLogVo.Log> {

    override fun map(input: ExpenseEnt): ExpenseLogVo.Log {

        return ExpenseLogVo.Log(
            ExpenseVto(
                id = input.expenseId,
                name = input.name ?: "",
                date = dateFormatter.format(input.modifiedDate),
                amount = currencyFormatter.format(input.amount),
                finance = "",
                category = categoryProvider.getCategoryDrawableByID(input.category),
                currencySymbol = "MMK"
            ), 0
        )
    }

}
