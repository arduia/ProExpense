package com.arduia.expense.storage.repository

import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toBoolean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads whether the DB has been rotated off its random first-launch key onto a user PIN (C6/A2).
 * Drives the Home PIN-setup banner: while [hasPinConfigured] is false, no user PIN exists yet.
 */
internal class SqlDelightSecurityStateReader(
    private val database: ProExpenseDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SecurityStateReader {

    private val queries get() = database.appMetaQueries

    override suspend fun hasPinConfigured(): Boolean = withContext(dispatcher) {
        queries.ensureMeta()
        queries.selectMeta().executeAsOne().pin_configured.toBoolean()
    }
}
