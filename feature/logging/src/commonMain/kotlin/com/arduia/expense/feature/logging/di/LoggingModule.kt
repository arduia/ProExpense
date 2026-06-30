package com.arduia.expense.feature.logging.di

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.feature.logging.DefaultLoggingRepository
import com.arduia.expense.feature.logging.LoadExpenseForEditUseCase
import com.arduia.expense.feature.logging.LogExpenseUseCase
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.LoggingViewModel
import com.arduia.expense.feature.logging.ObserveTagOptionsUseCase
import com.arduia.expense.feature.logging.UpdateExpenseUseCase
import org.koin.dsl.module

val loggingModule = module {
    single<LoggingRepository> { DefaultLoggingRepository(get<FinanceRecordRepository>()) }
    factory { ObserveTagOptionsUseCase(get<EventRepository>(), get<DebtRepository>()) }
    factory { LogExpenseUseCase(get<LoggingRepository>()) }
    factory { UpdateExpenseUseCase(get<FinanceRecordRepository>()) }
    factory { LoadExpenseForEditUseCase(get<FinanceRecordRepository>()) }
    factory {
        LoggingViewModel(
            observeTagOptions = get(),
            logExpense = get(),
            updateExpense = get(),
            loadExpenseForEdit = get(),
        )
    }
}
