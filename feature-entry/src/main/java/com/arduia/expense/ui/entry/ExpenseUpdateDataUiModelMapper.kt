package com.arduia.expense.ui.entry

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class ExpenseUpdateDataUiModelMapper @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider
) : Mapper<ExpenseEnt, ExpenseUpdateDataUiModel> {

    private val decimalFormat =
        (NumberFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat).apply {
            isGroupingUsed = false
        }

    override fun map(input: ExpenseEnt) = ExpenseUpdateDataUiModel(
        id = input.expenseId,
        name = input.name ?: "",
        date = input.createdDate,
        amount = input.amount.getActual().updateFormat(),
        category = categoryProvider.getCategoryByID(input.category),
        note = input.note ?: ""
    )

    private fun BigDecimal.updateFormat(): String {
        return decimalFormat.format(this)
    }

}
