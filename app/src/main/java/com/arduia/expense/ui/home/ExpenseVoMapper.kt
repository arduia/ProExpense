package com.arduia.expense.ui.home

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.vto.ExpenseVto
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Provider

class ExpenseVoMapper(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider,
    private val currencyProvider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseVto> {

    override fun map(input: ExpenseEnt): ExpenseVto =
        ExpenseVto(
            id = input.expenseId,
            name = input.name ?: "",
            date = dateFormatter.format(input.modifiedDate),
            amount = currencyFormatter.format(input.amount.getActual()),
            finance = "",
            category = categoryProvider.getCategoryDrawableByID(input.category),
            currencySymbol = currencyProvider.get()
        )

    class ExpenseVoMapperFactoryImpl(
        @CurrencyDecimalFormat
        private val currencyFormatter: NumberFormat,
        private val dateFormatter: DateFormatter,
        private val categoryProvider: ExpenseCategoryProvider
    ) : ExpenseVoMapperFactory {

        override fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseVto> {
            return ExpenseVoMapper(
                currencyFormatter,
                dateFormatter,
                categoryProvider,
                provider
            )
        }
    }
}

fun interface CurrencyProvider : Provider<String>

interface ExpenseVoMapperFactory : Mapper.Factory<ExpenseEnt, ExpenseVto> {
    fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseVto>
}