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

class ExpenseLogVoMapper  constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: DateFormatter,
    private val currencyFormatter: NumberFormat,
    private val provider: CurrencyProvider
) : Mapper<ExpenseEnt, ExpenseLogVo.Log> {

    override fun map(input: ExpenseEnt): ExpenseLogVo.Log {

        return ExpenseLogVo.Log(
            ExpenseVto(
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

class ExpenseLogVoMapperFactoryImpl @Inject constructor(
    private val categoryProvider: ExpenseCategoryProvider,
    private val dateFormatter: DateFormatter,
    @CurrencyDecimalFormat private val currencyFormatter: NumberFormat
) : ExpenseLogVoMapperFactory {
    override fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogVo.Log> {
        return ExpenseLogVoMapper(
            categoryProvider,
            dateFormatter,
            currencyFormatter,
            provider
        )
    }
}

interface ExpenseLogVoMapperFactory : Mapper.Factory<ExpenseEnt, ExpenseLogVo.Log> {
    fun create(provider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogVo.Log>
}