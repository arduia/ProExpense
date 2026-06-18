package com.arduia.expense.storage

import com.arduia.expense.domain.FinanceRecord
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryFinanceStore {
    private val mutex = Mutex()
    private val records = mutableListOf<FinanceRecord>()

    suspend fun insert(record: FinanceRecord) {
        mutex.withLock {
            records.add(0, record)
        }
    }

    suspend fun update(record: FinanceRecord) {
        mutex.withLock {
            val index = records.indexOfFirst { it.id == record.id }
            if (index >= 0) {
                records[index] = record
            }
        }
    }

    suspend fun delete(id: String) {
        mutex.withLock {
            records.removeAll { it.id == id }
        }
    }

    suspend fun allRecords(): List<FinanceRecord> = mutex.withLock {
        records.toList()
    }
}
