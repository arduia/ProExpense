package com.arduia.expense.storage

import android.content.Context
import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DEFAULT_CATEGORIES
import com.arduia.expense.domain.RecordIntegrityVerifier
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.repository.AppMetaBudgetRepository
import com.arduia.expense.storage.repository.AppMetaCurrencySettingsRepository
import com.arduia.expense.storage.repository.AppMetaLocalStore
import com.arduia.expense.storage.repository.AppMetaLockoutRepository
import com.arduia.expense.storage.repository.AppMetaSecurityStateReader
import com.arduia.expense.storage.repository.SqlDelightCategoryRepository
import com.arduia.expense.storage.repository.SqlDelightDebtRepository
import com.arduia.expense.storage.repository.SqlDelightEventRepository
import com.arduia.expense.storage.repository.SqlDelightFinanceRecordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Encapsulated storage composition: opens the encrypted database and exposes only `core:data`
 * contracts so SQLDelight/SQLCipher types never leak past `core:storage`. The app composition root
 * builds feature repositories on top of these.
 */
class ProExpenseStorage internal constructor(
    private val database: ProExpenseDatabase,
    val appMetaStore: AppMetaLocalStore,
    private val dispatcher: CoroutineDispatcher,
    val financeRecordRepository: FinanceRecordRepository,
    val categoryRepository: CategoryRepository,
    val eventRepository: EventRepository,
    val debtRepository: DebtRepository,
    val budgetRepository: BudgetRepository,
    val lockoutRepository: LockoutRepository,
    val securityStateReader: SecurityStateReader,
    val currencySettingsRepository: CurrencySettingsRepository,
) {

    /** Idempotently inserts the built-in categories (INSERT OR IGNORE) — safe to call every launch. */
    suspend fun seedDefaultCategories() = withContext(dispatcher) {
        database.categoryQueries.transaction {
            DEFAULT_CATEGORIES.forEach { category ->
                database.categoryQueries.seedCategory(
                    id = category.id.value,
                    name = category.name,
                    is_custom = if (category.isCustom) 1L else 0L,
                )
            }
        }
    }

    companion object {
        private const val DEFAULT_HOME_CURRENCY = "USD"

        fun create(
            context: Context,
            keyManager: DatabaseKeyManager = AndroidDatabaseKeyManager(context.applicationContext),
            dispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): ProExpenseStorage {
            val passphrase = keyManager.getOrCreateDatabaseKey()
            val driver = DatabaseDriverFactory(context.applicationContext).createDriver(passphrase)
            val database = ProExpenseDatabase(driver)
            val appMetaStore = AppMetaLocalStore(database.appMetaQueries, dispatcher)
            val integrityVerifier = RecordIntegrityVerifier()
            return ProExpenseStorage(
                database = database,
                appMetaStore = appMetaStore,
                dispatcher = dispatcher,
                financeRecordRepository = SqlDelightFinanceRecordRepository(
                    queries = database.financeRecordQueries,
                    eventQueries = database.eventQueries,
                    integrityVerifier = integrityVerifier,
                    dispatcher = dispatcher,
                ),
                categoryRepository = SqlDelightCategoryRepository(database.categoryQueries, dispatcher),
                eventRepository = SqlDelightEventRepository(database.eventQueries, dispatcher),
                debtRepository = SqlDelightDebtRepository(database.debtQueries, dispatcher),
                budgetRepository = AppMetaBudgetRepository(appMetaStore),
                lockoutRepository = AppMetaLockoutRepository(appMetaStore),
                securityStateReader = AppMetaSecurityStateReader(appMetaStore),
                currencySettingsRepository = AppMetaCurrencySettingsRepository(appMetaStore),
            )
        }
    }
}
