package com.arduia.expense.ui.expenselogs

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.formatter.DateFormatter
import com.arduia.expense.ui.home.CurrencyProvider
import java.math.BigDecimal
import java.text.NumberFormat
import javax.inject.Inject

class ExpenseLogUiModelMapper  constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: DateFormatter,
    private val currencyFormatter: NumberFormat,
    private val provider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseLogUiModel.Log> {

    override fun map(input: ExpenseEnt): ExpenseLogUiModel.Log {

        return ExpenseLogUiModel.Log(
            ExpenseUiModel(
                id = input.expenseId,
                name = input.name ?: "",
                date = dateFormatter.format(input.modifiedDate),
                amount = currencyFormatter.format(
                    BigDecimal.valueOf(
                        input.amount.getActual().toDouble()
                    )
                ),
                finance = "",
                category = categoryProvider.getCategoryDrawableByID(input.category),
                currencySymbol = provider.get()
            ), 0
        )
    }

}

class ExpenseUiModelMapperFactoryImpl @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: DateFormatter,
    @CurrencyDecimalFormat private val currencyFormatter: NumberFormat
) : ExpenseUiModelMapperFactory {
    override fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogUiModel.Log> {
        return ExpenseLogUiModelMapper(
            categoryProvider,
            dateFormatter,
            currencyFormatter,
            provider
        )
    }
}

interface ExpenseUiModelMapperFactory : Mapper.Factory<ExpenseEnt, ExpenseLogUiModel.Log> {
    fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogUiModel.Log>
}