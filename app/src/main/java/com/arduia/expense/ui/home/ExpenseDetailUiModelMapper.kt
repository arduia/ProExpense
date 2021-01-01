package com.arduia.expense.ui.home

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import java.text.NumberFormat

class ExpenseDetailUiModelMapper(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider,
    private val currencyProvider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseDetailUiModel> {

    override fun map(input: ExpenseEnt): ExpenseDetailUiModel {
        return ExpenseDetailUiModel(
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

class ExpenseDetailUiModelMapperFactoryImpl(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider
): ExpenseDetailUiModelMapperFactory{
    override fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseDetailUiModel> {
        return ExpenseDetailUiModelMapper(
            currencyFormatter, dateFormatter, categoryProvider, currencyProvider
        )
    }
}

interface ExpenseDetailUiModelMapperFactory: Mapper.Factory<ExpenseEnt, ExpenseDetailUiModel>{
    fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseDetailUiModel>
}