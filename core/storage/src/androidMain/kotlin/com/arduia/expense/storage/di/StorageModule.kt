package com.arduia.expense.storage.di

import com.arduia.expense.storage.ProExpenseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Opens the encrypted database once; exposes `core:data` contracts only (no SQLDelight/SQLCipher leakage). */
val storageModule = module {
    single { ProExpenseStorage.create(androidContext()) }
    single { get<ProExpenseStorage>().financeRecordRepository }
    single { get<ProExpenseStorage>().categoryRepository }
    single { get<ProExpenseStorage>().eventRepository }
    single { get<ProExpenseStorage>().debtRepository }
    single { get<ProExpenseStorage>().budgetRepository }
    single { get<ProExpenseStorage>().lockoutRepository }
    single { get<ProExpenseStorage>().securityStateReader }
    single { get<ProExpenseStorage>().currencySettingsRepository }
    single { get<ProExpenseStorage>().sharedCostRepository }
    single { get<ProExpenseStorage>().importExportRepository }
    single { get<ProExpenseStorage>().clearDataRepository }
    single { get<ProExpenseStorage>().profileRepository }
    single { get<ProExpenseStorage>().localeRepository }
}
