package com.arduia.expense.ui.home

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.category.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.expenselogs.ExpenseUiModel
import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Provider

class ExpenseUiModelMapper(
    @CurrencyDecimalFormat
    private val currencyFormatter: NumberFormat,
    private val dateFormatter: DateFormatter,
    private val categoryProvider: ExpenseCategoryProvider,
    private val currencyProvider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseUiModel> {

    override fun map(input: ExpenseEnt): ExpenseUiModel =
        ExpenseUiModel(
            id = input.expenseId,
            name = input.name ?: "",
            date = dateFormatter.format(input.modifiedDate),
            amount = currencyFormatter.format(input.amount.getActual()),
            finance = "",
            category = categoryProvider.getCategoryDrawableByID(input.category),
            currencySymbol = currencyProvider.get()
        )

    class ExpenseUiModelMapperFactoryImpl @Inject constructor(
        @CurrencyDecimalFormat
        private val currencyFormatter: NumberFormat,
        private val dateFormatter: DateFormatter,
        private val categoryProvider: ExpenseCategoryProvider
    ) : ExpenseUiModelMapperFactory {

        override fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseUiModel> {
            return ExpenseUiModelMapper(
                currencyFormatter,
                dateFormatter,
                categoryProvider,
                provider
            )
        }
    }
}

fun interface CurrencyProvider : Provider<String>

interface ExpenseUiModelMapperFactory : Mapper.Factory<ExpenseEnt, ExpenseUiModel> {
    fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseUiModel>
}