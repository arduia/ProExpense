package com.arduia.expense.feature.history.di

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.feature.history.DefaultHistoryRepository
import com.arduia.expense.feature.history.HistoryRepository
import org.koin.dsl.module

val historyModule = module {
    single<HistoryRepository> { DefaultHistoryRepository(get<FinanceRecordRepository>()) }
}
