package com.arduia.expense.storage.repository

import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlDelightSecurityStateReaderTest {

    private val database = newTestDatabase()
    private val reader = SqlDelightSecurityStateReader(database, UnconfinedTestDispatcher())

    // Rule: before any PIN is set, the DB is still on its random first-launch key.
    @Test
    fun freshDatabase_hasNoPinConfigured() = runTest {
        assertFalse(reader.hasPinConfigured())
    }

    // Rule: once the PIN-configured flag is set (by the security feature on rotation), it reports true.
    @Test
    fun afterPinConfiguredFlagSet_returnsTrue() = runTest {
        database.appMetaQueries.updatePinConfigured(1L)

        assertTrue(reader.hasPinConfigured())
    }
}
