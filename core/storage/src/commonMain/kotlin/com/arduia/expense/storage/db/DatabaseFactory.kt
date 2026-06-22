package com.arduia.expense.storage.db

/**
 * Builds an encrypted [ProExpenseDatabase] and bootstraps it. This is the single seam the
 * app/DI layer wires (F17): it never exposes the raw driver or queries to features.
 */
fun createProExpenseDatabase(
    driverFactory: DatabaseDriverFactory,
    passphrase: ByteArray,
): ProExpenseDatabase {
    val driver = driverFactory.createDriver(passphrase)
    return ProExpenseDatabase(driver).also { it.bootstrap() }
}

/**
 * Ensures the database is in a usable state: the single [app_meta] row exists and the default
 * category set has been seeded once. Idempotent — safe to call on every app start and from tests.
 */
fun ProExpenseDatabase.bootstrap() {
    appMetaQueries.ensureMeta()
    if (categoryQueries.countCategories().executeAsOne() == 0L) {
        DefaultCategories.all.forEach { category ->
            categoryQueries.insertCategory(
                id = category.id,
                name = category.name,
                is_custom = 0L,
            )
        }
    }
}
