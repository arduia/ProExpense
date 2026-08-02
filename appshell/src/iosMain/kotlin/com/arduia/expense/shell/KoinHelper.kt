package com.arduia.expense.shell

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.feature.logging.LoggingViewModel
import org.koin.mp.KoinPlatform

/**
 * Typed resolvers for Swift.
 *
 * Koin's `get<T>()` is a reified inline function, which Kotlin/Native cannot export to Objective-C —
 * every dependency SwiftUI needs must be surfaced through a concrete-typed function like these.
 */
object KoinHelper {
    fun resolveAppShell(): AppShellViewModel = KoinPlatform.getKoin().get()

    fun resolveHome(): HomeViewModel = KoinPlatform.getKoin().get()

    fun resolveJournal(): JournalViewModel = KoinPlatform.getKoin().get()

    fun resolveAddExpense(): AddExpenseViewModel = KoinPlatform.getKoin().get()

    fun resolveLogging(): LoggingViewModel = KoinPlatform.getKoin().get()

    fun resolveRecords(): FinanceRecordRepository = KoinPlatform.getKoin().get()
}
