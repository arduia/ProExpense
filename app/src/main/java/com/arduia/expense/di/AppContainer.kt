package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.feature.logging.DefaultLoggingRepository
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.storage.ProExpenseStorage

/**
 * Manual composition root (design §7). No DI framework: it builds the encrypted storage, then the
 * thin feature repositories on top, exposed as plain properties for ViewModels to consume. Revisit
 * Koin only if/when the iosApp graph duplicates this wiring.
 */
class AppContainer(context: Context) {

    private val storage: ProExpenseStorage = ProExpenseStorage.create(context.applicationContext)

    val financeRecordRepository: FinanceRecordRepository = storage.financeRecordRepository
    val categoryRepository: CategoryRepository = storage.categoryRepository
    val eventRepository: EventRepository = storage.eventRepository
    val debtRepository: DebtRepository = storage.debtRepository
    val budgetRepository: BudgetRepository = storage.budgetRepository
    val lockoutRepository: LockoutRepository = storage.lockoutRepository
    val securityStateReader: SecurityStateReader = storage.securityStateReader

    val loggingRepository: LoggingRepository = DefaultLoggingRepository(financeRecordRepository)

    /** First-launch data bootstrap — seeds built-in categories. Safe to call on every start. */
    suspend fun initialize() {
        storage.seedDefaultCategories()
    }
}
