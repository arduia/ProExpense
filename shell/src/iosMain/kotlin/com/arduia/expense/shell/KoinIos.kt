package com.arduia.expense.shell

import com.arduia.expense.feature.categories.di.categoriesModule
import com.arduia.expense.feature.currency.di.currencyModule
import com.arduia.expense.feature.debt.di.debtModule
import com.arduia.expense.feature.eventbudget.di.eventBudgetModule
import com.arduia.expense.feature.history.di.historyModule
import com.arduia.expense.feature.importexport.di.importExportModule
import com.arduia.expense.feature.logging.LoggingViewModel
import com.arduia.expense.feature.logging.di.loggingModule
import com.arduia.expense.feature.onboarding.di.onboardingModule
import com.arduia.expense.feature.reports.di.reportsModule
import com.arduia.expense.feature.sharedcost.di.sharedCostModule
import com.arduia.expense.shell.di.shellModule
import com.arduia.expense.shell.home.HomeViewModel
import com.arduia.expense.storage.di.storageModule
import org.koin.core.context.startKoin

/**
 * iOS composition root — the counterpart to `ExpenseApplication.ensureStarted()` on Android. Opens
 * the encrypted database (Keychain-held key) and registers every shared feature module.
 *
 * Two Android modules are deliberately absent, and the iOS shell must not offer their surfaces yet:
 * - `authModule` — `PinAuthRepositoryImpl` hashes with `javax.crypto` PBKDF2 and lives in
 *   androidMain, so there is no iOS PIN gate until a CommonCrypto/Keychain actual exists.
 * - `syncModule`/`syncPlatformModule` — Google Drive OAuth is Android-only for now.
 *
 * See `docs/ios_compatibility_plan.md` Phase 2 for both follow-ups.
 */
fun startProExpenseKoin() {
    startKoin {
        modules(
            storageModule,
            loggingModule,
            historyModule,
            currencyModule,
            eventBudgetModule,
            reportsModule,
            debtModule,
            sharedCostModule,
            categoriesModule,
            importExportModule,
            onboardingModule,
            shellModule,
        )
    }
}

/** Swift entry point for the shared ViewModels — Koin's own API is awkward to call across the bridge. */
object ProExpenseShell : org.koin.core.component.KoinComponent {
    fun homeViewModel(): HomeViewModel = getKoin().get()

    fun loggingViewModel(): LoggingViewModel = getKoin().get()
}
