package com.arduia.expense.di

import com.arduia.expense.data.DatabaseKeyProvider
import com.arduia.expense.storage.StorageComponent
import com.arduia.expense.storage.db.DatabaseDriverFactory
import com.arduia.expense.ui.currency.CurrencyViewModel
import com.arduia.expense.ui.events.EventsViewModel
import com.arduia.expense.ui.home.HomeViewModel
import com.arduia.expense.ui.journal.JournalViewModel
import com.arduia.expense.ui.logging.ExpenseEntryViewModel
import com.arduia.expense.ui.reports.ReportsViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Android-side Koin graph. Provides the platform [StorageComponent] (encrypted driver + Keystore
 * passphrase) and the screen presentation models. The repository contracts themselves come from
 * `core:storage`'s shared [com.arduia.expense.storage.di.repositoryModule], so nothing here sees
 * SQLDelight. The [StorageComponent] is a lazy `single`, so the DB only opens when a screen first
 * resolves a repository — never in previews or screenshot tests.
 */
val platformStorageModule = module {
    single { DatabaseKeyProvider(androidContext()) }
    single { DatabaseDriverFactory(androidContext()) }
    single { StorageComponent.create(get(), get<DatabaseKeyProvider>().getOrCreateKey()) }
}

/**
 * Presentation models. Those that own a coroutine scope are parameterized factories so the caller
 * can hand in the composition scope (`rememberCoroutineScope()`); tests construct them directly.
 */
val viewModelModule = module {
    factory { (scope: CoroutineScope) ->
        HomeViewModel(
            financeRepository = get(),
            budgetRepository = get(),
            profileRepository = get(),
            securityStateReader = get(),
            scope = scope,
        )
    }
    factory { (scope: CoroutineScope) ->
        JournalViewModel(
            financeRepository = get(),
            profileRepository = get(),
            scope = scope,
        )
    }
    factory { (scope: CoroutineScope) ->
        ReportsViewModel(
            financeRepository = get(),
            profileRepository = get(),
            scope = scope,
        )
    }
    factory { (scope: CoroutineScope) ->
        CurrencyViewModel(profileRepository = get(), scope = scope)
    }
    factory { (scope: CoroutineScope) ->
        EventsViewModel(
            eventRepository = get(),
            financeRepository = get(),
            profileRepository = get(),
            scope = scope,
        )
    }
    factoryOf(::ExpenseEntryViewModel)
}
