package com.arduia.expense.storage

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.db.DatabaseDriverFactory
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.db.createProExpenseDatabase
import com.arduia.expense.storage.repository.SqlDelightBudgetRepository
import com.arduia.expense.storage.repository.SqlDelightCategoryRepository
import com.arduia.expense.storage.repository.SqlDelightDebtRepository
import com.arduia.expense.storage.repository.SqlDelightEventRepository
import com.arduia.expense.storage.repository.SqlDelightFinanceRecordRepository
import com.arduia.expense.storage.repository.SqlDelightLockoutRepository
import com.arduia.expense.storage.repository.SqlDelightProfileRepository
import com.arduia.expense.storage.repository.SqlDelightSecurityStateReader

/**
 * The public face of the data layer (F17): the app/DI constructs this once with a platform
 * [DatabaseDriverFactory] + passphrase, then hands the individual repository contracts to
 * features. Nothing above this exposes SQLDelight types, so the repository pattern stays the
 * only data-access path and features never see the database.
 */
class StorageComponent private constructor(
    private val database: ProExpenseDatabase,
) {
    val financeRecordRepository: FinanceRecordRepository by lazy {
        SqlDelightFinanceRecordRepository(database)
    }
    val eventRepository: EventRepository by lazy {
        SqlDelightEventRepository(database)
    }
    val debtRepository: DebtRepository by lazy {
        SqlDelightDebtRepository(database)
    }
    val categoryRepository: CategoryRepository by lazy {
        SqlDelightCategoryRepository(database)
    }
    val budgetRepository: BudgetRepository by lazy {
        SqlDelightBudgetRepository(database)
    }
    val lockoutRepository: LockoutRepository by lazy {
        SqlDelightLockoutRepository(database)
    }
    val securityStateReader: SecurityStateReader by lazy {
        SqlDelightSecurityStateReader(database)
    }
    val profileRepository: ProfileRepository by lazy {
        SqlDelightProfileRepository(database)
    }

    companion object {
        /** Production entry point: opens (creating + bootstrapping if needed) the encrypted DB. */
        fun create(driverFactory: DatabaseDriverFactory, passphrase: ByteArray): StorageComponent =
            StorageComponent(createProExpenseDatabase(driverFactory, passphrase))

        /** Wraps an already-open database (used by tests with an in-memory driver). */
        fun fromDatabase(database: ProExpenseDatabase): StorageComponent =
            StorageComponent(database)
    }
}
