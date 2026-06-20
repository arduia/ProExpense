package com.arduia.expense.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightSnapshotPersistence(
    private val context: Context,
    private val store: InMemoryDataStore,
) {
    private var persistenceAvailable = true

    suspend fun persist() = withContext(Dispatchers.IO) {
        if (!persistenceAvailable) return@withContext
        runCatching {
            val key = store.getActiveKey() ?: ByteArray(32)
            val database = SqlCipherDatabaseFactory.open(context, key)
            val payload = StoreSnapshotCodec.encode(store.exportSnapshot())
            database.appSnapshotQueries.insertSnapshot(payload)
        }.onFailure {
            persistenceAvailable = false
        }
    }

    suspend fun restoreIfPresent() = withContext(Dispatchers.IO) {
        if (!persistenceAvailable) return@withContext
        runCatching {
            val key = store.getActiveKey() ?: ByteArray(32)
            val database = SqlCipherDatabaseFactory.open(context, key)
            val payload = database.appSnapshotQueries.selectSnapshot().executeAsOneOrNull()
            if (payload != null) {
                store.importSnapshot(StoreSnapshotCodec.decode(payload))
            }
        }.onFailure {
            persistenceAvailable = false
        }
    }
}
