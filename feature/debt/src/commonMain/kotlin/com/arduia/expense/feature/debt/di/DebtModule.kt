package com.arduia.expense.feature.debt.di

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.feature.debt.AggregateDebtsUseCase
import com.arduia.expense.feature.debt.CreateDebtUseCase
import com.arduia.expense.feature.debt.DeleteDebtUseCase
import com.arduia.expense.feature.debt.SettleDebtUseCase
import kotlinx.datetime.Clock
import org.koin.dsl.module

val debtModule = module {
    factory { AggregateDebtsUseCase() }
    factory { CreateDebtUseCase(get<DebtRepository>(), nowEpochMillis = { Clock.System.now().toEpochMilliseconds() }) }
    factory { DeleteDebtUseCase(get<DebtRepository>()) }
    factory { SettleDebtUseCase(get<DebtRepository>()) }
}
