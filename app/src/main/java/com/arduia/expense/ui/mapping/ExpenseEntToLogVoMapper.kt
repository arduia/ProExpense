package com.arduia.expense.ui.mapping

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.expense.ExpenseLogVo
import com.arduia.expense.ui.expense.mapper.ExpenseLogVoMapperFactory
import com.arduia.expense.ui.home.CurrencyProvider
import javax.inject.Inject

class ExpenseEntToLogVoMapper @Inject constructor(private val mapper: Mapper<ExpenseEnt, ExpenseLogVo.Log>) :
    Mapper<ExpenseEnt, ExpenseLogVo> {
    override fun map(input: ExpenseEnt): ExpenseLogVo {
        return mapper.map(input)
    }

    class ExpenseEntToLogVoMapperFactoryImpl @Inject constructor(private val factory: ExpenseLogVoMapperFactory): ExpenseEntToLogVoMapperFactory{
        override fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogVo> {
            return ExpenseEntToLogVoMapper(factory.create(currencyProvider))
        }
    }
}

interface ExpenseEntToLogVoMapperFactory: Mapper.Factory<ExpenseEnt, ExpenseLogVo>{
    fun create(currencyProvider: CurrencyProvider): Mapper<ExpenseEnt, ExpenseLogVo>
}