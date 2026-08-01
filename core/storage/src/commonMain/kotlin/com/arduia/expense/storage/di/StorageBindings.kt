package com.arduia.expense.storage.di

import com.arduia.expense.storage.ProExpenseStorage
import org.koin.dsl.module

/**
 * `core:data` contract bindings shared by every platform — all resolve off the single
 * `ProExpenseStorage` instance. Each platform provides its own module for constructing that
 * instance (driver/key-manager/key-value-store are platform-specific) and `includes` this one —
 * see `storageModule` in androidMain.
 */
val storageBindings =
    module {
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
        single { get<ProExpenseStorage>().defaultCategoryRepository }
        single { get<ProExpenseStorage>().themeRepository }
        single { get<ProExpenseStorage>().syncStateRepository }
        single { get<ProExpenseStorage>().syncAccountRepository }
    }
