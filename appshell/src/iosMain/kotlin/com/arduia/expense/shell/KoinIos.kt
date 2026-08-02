package com.arduia.expense.shell

import com.arduia.expense.feature.auth.di.authModule
import com.arduia.expense.feature.categories.di.categoriesModule
import com.arduia.expense.feature.currency.di.currencyModule
import com.arduia.expense.feature.debt.di.debtModule
import com.arduia.expense.feature.eventbudget.di.eventBudgetModule
import com.arduia.expense.feature.history.di.historyModule
import com.arduia.expense.feature.importexport.di.importExportModule
import com.arduia.expense.feature.logging.di.loggingModule
import com.arduia.expense.feature.onboarding.di.onboardingModule
import com.arduia.expense.feature.reports.di.reportsModule
import com.arduia.expense.feature.sharedcost.di.sharedCostModule
import com.arduia.expense.shell.di.shellModule
import com.arduia.expense.storage.ProExpenseStorage
import com.arduia.expense.storage.di.storageModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

private val initScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
private var started = false

/**
 * iOS counterpart to `ExpenseApplication.ensureStarted()` — same module list, same category seeding,
 * minus `androidContext()`.
 *
 * `:feature:sync` is deliberately absent: `syncModule`'s bindings need the Drive OAuth/transport
 * pieces that only exist in `syncPlatformModule` (androidMain), so registering it here would
 * resolve to missing definitions at runtime rather than fail at compile time.
 *
 * Call once from `ProExpenseApp.init` before any view resolves a repository.
 */
fun initKoinIos() {
    if (started) return
    started = true
    startKoin {
        modules(
            storageModule,
            loggingModule,
            historyModule,
            currencyModule,
            authModule,
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
    initScope.launch { KoinPlatform.getKoin().get<ProExpenseStorage>().seedDefaultCategories() }
}
