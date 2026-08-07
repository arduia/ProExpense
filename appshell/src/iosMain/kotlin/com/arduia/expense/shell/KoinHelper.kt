package com.arduia.expense.shell

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.feature.logging.LoggingViewModel
import org.koin.mp.KoinPlatform

/**
 * Typed resolvers for Swift.
 *
 * Koin's `get<T>()` is a reified inline function, which Kotlin/Native cannot export to Objective-C —
 * every dependency SwiftUI needs must be surfaced through a concrete-typed function like these.
 *
 * Necessarily over detekt's function threshold: this is a flat lookup table with one entry per
 * screen, so it grows with the screen count by design. Splitting it would just scatter the table.
 */
@Suppress("TooManyFunctions")
object KoinHelper {
    fun resolveAppShell(): AppShellViewModel = KoinPlatform.getKoin().get()

    fun resolveHome(): HomeViewModel = KoinPlatform.getKoin().get()

    fun resolveJournal(): JournalViewModel = KoinPlatform.getKoin().get()

    fun resolveJournalDetail(): JournalDetailViewModel = KoinPlatform.getKoin().get()

    fun resolveAddExpense(): AddExpenseViewModel = KoinPlatform.getKoin().get()

    fun resolveOnboarding(): OnboardingViewModel = KoinPlatform.getKoin().get()

    fun resolvePinEntry(): PinEntryViewModel = KoinPlatform.getKoin().get()

    fun resolvePinSetup(): PinSetupViewModel = KoinPlatform.getKoin().get()

    fun resolveMore(): MoreViewModel = KoinPlatform.getKoin().get()

    fun resolveCategories(): CategoriesViewModel = KoinPlatform.getKoin().get()

    fun resolveReports(): ReportsViewModel = KoinPlatform.getKoin().get()

    fun resolveEventBudget(): EventBudgetViewModel = KoinPlatform.getKoin().get()

    fun resolveDebt(): DebtViewModel = KoinPlatform.getKoin().get()

    fun resolveSharedCost(): SharedCostViewModel = KoinPlatform.getKoin().get()

    fun resolveLogging(): LoggingViewModel = KoinPlatform.getKoin().get()

    fun resolveRecords(): FinanceRecordRepository = KoinPlatform.getKoin().get()
}
