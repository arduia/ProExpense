package com.arduia.expense.storage.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.arduia.expense.storage.db.ProExpenseDatabase

/** In-memory, unencrypted database for storage-layer tests — exercises real SQL + mappers. */
internal fun inMemoryDatabase(): ProExpenseDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    ProExpenseDatabase.Schema.create(driver)
    return ProExpenseDatabase(driver)
}
