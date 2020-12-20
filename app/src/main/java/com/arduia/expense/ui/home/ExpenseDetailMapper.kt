package com.arduia.expense.ui.home

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import timber.log.Timber
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ExpenseDetailMapper(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider,
    private val currencyProvider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseDetailsVto> {

    override fun map(input: ExpenseEnt): ExpenseDetailsVto {
        return ExpenseDetailsVto(
            id = input.expenseId,
            name = input.name ?: "",
            date = dateFormatter.format(input.modifiedDate),
            amount = currencyFormatter.format(input.amount.getActual()),
            finance = "",
            category = categoryProvider.getCategoryDrawableByID(input.category),
            note = input.note ?: "",
            symbol = currencyProvider.get()
        )
    }

}

class ExpenseDetailMapperFactoryImpl(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider
): ExpenseDetailMapperFactory{
    override fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseDetailsVto> {
        return ExpenseDetailMapper(
            currencyFormatter, dateFormatter, categoryProvider, currencyProvider
        )
    }
}

interface ExpenseDetailMapperFactory: Mapper.Factory<ExpenseEnt, ExpenseDetailsVto>{
    fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseDetailsVto>
}