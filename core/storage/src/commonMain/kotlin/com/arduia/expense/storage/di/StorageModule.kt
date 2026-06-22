package com.arduia.expense.storage.di

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.StorageComponent
import org.koin.dsl.module

/**
 * KMP Koin module exposing every repository contract sourced from the single [StorageComponent].
 * The platform layer is responsible for providing the [StorageComponent] (it needs a platform
 * driver + passphrase); this module keeps the SQLDelight types behind the contracts (F17) and lets
 * features depend only on `core:data` interfaces resolved through DI.
 */
val repositoryModule = module {
    single<FinanceRecordRepository> { get<StorageComponent>().financeRecordRepository }
    single<EventRepository> { get<StorageComponent>().eventRepository }
    single<DebtRepository> { get<StorageComponent>().debtRepository }
    single<CategoryRepository> { get<StorageComponent>().categoryRepository }
    single<BudgetRepository> { get<StorageComponent>().budgetRepository }
    single<LockoutRepository> { get<StorageComponent>().lockoutRepository }
    single<SecurityStateReader> { get<StorageComponent>().securityStateReader }
    single<ProfileRepository> { get<StorageComponent>().profileRepository }
}
