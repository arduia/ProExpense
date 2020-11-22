package com.arduia.expense.ui.home

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ExpenseDetailMapper(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormat,
    private val categoryProvider: ExpenseCategoryProvider,
    private val currencyRepository: CurrencyRepository
) : Mapper<ExpenseEnt, ExpenseDetailsVto> {

    private val symbol by lazy {
        currencyRepository.getSelectedCacheCurrency().awaitValueOrError().symbol
    }

    override fun map(input: ExpenseEnt): ExpenseDetailsVto {
        return ExpenseDetailsVto(
            id = input.expenseId,
            name = input.name ?: "",
            date = dateFormatter.format(input.modifiedDate),
            amount = currencyFormatter.format(input.amount),
            finance = "",
            category = categoryProvider.getCategoryDrawableByID(input.category),
            note = input.note ?: "",
            symbol = symbol
        )
    }

}