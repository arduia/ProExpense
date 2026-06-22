package com.arduia.expense.storage

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.arduia.expense.data.Result
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.db.bootstrap
import kotlin.test.fail

/**
 * Builds a fresh in-memory database with the real generated schema and runs [bootstrap] once,
 * mirroring what production does after opening the encrypted file. Each call is fully isolated.
 */
internal fun newTestDatabase(): ProExpenseDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    ProExpenseDatabase.Schema.create(driver)
    return ProExpenseDatabase(driver).also { it.bootstrap() }
}

internal fun <T> Result<T>.getOrFail(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> fail("Expected Success but was Error: $message")
}
