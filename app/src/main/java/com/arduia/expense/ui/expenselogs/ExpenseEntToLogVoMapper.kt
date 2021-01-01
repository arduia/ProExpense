package com.arduia.expense.ui.expenselogs

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.home.CurrencyProvider
import javax.inject.Inject

class ExpenseEntToLogVoMapper @Inject constructor(private val mapper: Mapper<ExpenseEnt, ExpenseLogUiModel.Log>) :
    Mapper<ExpenseEnt, ExpenseLogUiModel> {
    override fun map(input: ExpenseEnt): ExpenseLogUiModel {
        return mapper.map(input)
    }

    class ExpenseEntToLogVoMapperFactoryImpl @Inject constructor(private val factory: ExpenseUiModelMapperFactory):
        ExpenseEntToLogVoMapperFactory {
        override fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogUiModel> {
            return ExpenseEntToLogVoMapper(factory.create(currencyProvider))
        }
    }
}

interface ExpenseEntToLogVoMapperFactory: Mapper.Factory<ExpenseEnt, ExpenseLogUiModel>{
    fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogUiModel>
}