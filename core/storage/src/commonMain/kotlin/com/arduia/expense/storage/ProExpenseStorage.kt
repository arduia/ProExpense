package com.arduia.expense.storage

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ImportExportRepository
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.domain.DEFAULT_CATEGORIES
import com.arduia.expense.domain.RecordIntegrityVerifier
import com.arduia.expense.domain.toCode
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.repository.AppMetaBudgetRepository
import com.arduia.expense.storage.repository.AppMetaCurrencySettingsRepository
import com.arduia.expense.storage.repository.AppMetaDefaultCategoryRepository
import com.arduia.expense.storage.repository.AppMetaLocalStore
import com.arduia.expense.storage.repository.AppMetaLocaleRepository
import com.arduia.expense.storage.repository.AppMetaLockoutRepository
import com.arduia.expense.storage.repository.AppMetaProfileRepository
import com.arduia.expense.storage.repository.AppMetaSecurityStateReader
import com.arduia.expense.storage.repository.AppMetaThemeRepository
import com.arduia.expense.storage.repository.SqlDelightCategoryRepository
import com.arduia.expense.storage.repository.SqlDelightClearDataRepository
import com.arduia.expense.storage.repository.SqlDelightDebtRepository
import com.arduia.expense.storage.repository.SqlDelightEventRepository
import com.arduia.expense.storage.repository.SqlDelightFinanceRecordRepository
import com.arduia.expense.storage.repository.SqlDelightImportExportRepository
import com.arduia.expense.storage.repository.SqlDelightSharedCostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Encapsulated storage composition: opens the encrypted database and exposes only `core:data`
 * contracts so SQLDelight/SQLCipher types never leak past `core:storage`. The app composition root
 * builds feature repositories on top of these. Portable across Android/iOS — platform-specific
 * pieces (driver, key manager, key-value store) are injected by [create]; each platform exposes its
 * own convenience overload (see `ProExpenseStorage.create(context)` in androidMain).
 */
class ProExpenseStorage internal constructor(
    val database: ProExpenseDatabase,
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
    val sharedCostRepository: SharedCostRepository,
    val importExportRepository: ImportExportRepository,
    val clearDataRepository: ClearDataRepository,
    val profileRepository: ProfileRepository,
    val localeRepository: LocaleRepository,
    val defaultCategoryRepository: DefaultCategoryRepository,
    val themeRepository: ThemeRepository,
) {
    /** Idempotently inserts the built-in categories (INSERT OR IGNORE) — safe to call every launch. */
    suspend fun seedDefaultCategories() =
        withContext(dispatcher) {
            database.categoryQueries.transaction {
                DEFAULT_CATEGORIES.forEachIndexed { index, category ->
                    database.categoryQueries.seedCategory(
                        id = category.id.value,
                        name = category.name,
                        is_custom = if (category.isCustom) 1L else 0L,
                        sort_order = index.toLong(),
                        icon_id = "",
                        type = category.type.toCode(),
                    )
                }
            }
        }

    companion object {
        /**
         * Portable core: platform pieces (driver, key manager, key-value cache) are injected so
         * this composition step has no Context/Keychain dependency of its own. Platforms add a
         * convenience overload building those pieces from their native primitives — see
         * `ProExpenseStorage.create(context)` (androidMain) / `ProExpenseStorage.create()` (iosMain).
         */
        fun create(
            driverFactory: DatabaseDriverFactory,
            keyManager: DatabaseKeyManager,
            keyValueStore: PlatformKeyValueStore,
            dispatcher: CoroutineDispatcher = Dispatchers.Default,
        ): ProExpenseStorage {
            val passphrase = keyManager.getOrCreateDatabaseKey()
            val driver = driverFactory.createDriver(passphrase)
            val database = ProExpenseDatabase(driver)
            val appMetaStore = AppMetaLocalStore(database.appMetaQueries, dispatcher)
            val integrityVerifier = RecordIntegrityVerifier()
            val financeRecordRepository =
                SqlDelightFinanceRecordRepository(
                    queries = database.financeRecordQueries,
                    eventQueries = database.eventQueries,
                    integrityVerifier = integrityVerifier,
                    dispatcher = dispatcher,
                )
            val eventRepository = SqlDelightEventRepository(database.eventQueries, dispatcher)
            val debtRepository =
                SqlDelightDebtRepository(
                    queries = database.debtQueries,
                    financeRecordRepository = financeRecordRepository,
                    dispatcher = dispatcher,
                )
            val sharedCostRepository =
                SqlDelightSharedCostRepository(
                    queries = database.sharedCostQueries,
                    financeRecordRepository = financeRecordRepository,
                    dispatcher = dispatcher,
                )
            return ProExpenseStorage(
                database = database,
                appMetaStore = appMetaStore,
                dispatcher = dispatcher,
                financeRecordRepository = financeRecordRepository,
                categoryRepository = SqlDelightCategoryRepository(database.categoryQueries, dispatcher),
                eventRepository = eventRepository,
                debtRepository = debtRepository,
                budgetRepository = AppMetaBudgetRepository(appMetaStore),
                lockoutRepository = AppMetaLockoutRepository(appMetaStore),
                securityStateReader = AppMetaSecurityStateReader(appMetaStore),
                currencySettingsRepository = AppMetaCurrencySettingsRepository(appMetaStore),
                sharedCostRepository = sharedCostRepository,
                importExportRepository =
                    SqlDelightImportExportRepository(
                        financeRecordRepository = financeRecordRepository,
                        eventRepository = eventRepository,
                        debtRepository = debtRepository,
                        sharedCostRepository = sharedCostRepository,
                        dispatcher = dispatcher,
                    ),
                clearDataRepository = SqlDelightClearDataRepository(database, dispatcher),
                profileRepository = AppMetaProfileRepository(appMetaStore, keyValueStore),
                localeRepository = AppMetaLocaleRepository(appMetaStore, keyValueStore),
                defaultCategoryRepository = AppMetaDefaultCategoryRepository(appMetaStore),
                themeRepository = AppMetaThemeRepository(appMetaStore),
            )
        }
    }
}
