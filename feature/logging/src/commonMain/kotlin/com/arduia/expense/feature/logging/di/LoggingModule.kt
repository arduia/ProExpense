package com.arduia.expense.feature.logging.di

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.feature.logging.DefaultLoggingRepository
import com.arduia.expense.feature.logging.LoggingRepository
import org.koin.dsl.module

val loggingModule = module {
    single<LoggingRepository> { DefaultLoggingRepository(get<FinanceRecordRepository>()) }
}
