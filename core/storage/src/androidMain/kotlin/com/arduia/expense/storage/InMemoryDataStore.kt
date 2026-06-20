package com.arduia.expense.storage

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDataStore : KeyRotationStore, EntityDataStore {
    private val mutex = Mutex()
    private val records = mutableListOf<FinanceRecord>()
    private val events = mutableListOf<Event>()
    private val debts = mutableListOf<Debt>()
    private val sharedCosts = mutableListOf<SharedCost>()

    override var monthlyBudgetCents: Long? = null
    override var homeCurrencyCode: String = "USD"
    override var failedAttemptCount: Int = 0
    override var lockoutUntilEpochMillis: Long? = null
    override var securityAnswerHash: String? = null
    override var securityQuestionId: String? = null
    override var biometricEnrolled: Boolean = false

    private var activeKey: ByteArray? = null
    private var pinSalt: ByteArray? = null
    private var pinProtected: Boolean = false
    private var biometricWrappedKey: ByteArray? = null

    override suspend fun persistSettings() = Unit

    override suspend fun getActiveKey(): ByteArray? = mutex.withLock { activeKey?.copyOf() }

    override suspend fun getPinSalt(): ByteArray? = mutex.withLock { pinSalt?.copyOf() }

    override suspend fun setPinDerivedKey(key: ByteArray, salt: ByteArray) = mutex.withLock {
        activeKey = key.copyOf()
        pinSalt = salt.copyOf()
        pinProtected = true
    }

    override suspend fun setRandomKey(key: ByteArray) = mutex.withLock {
        activeKey = key.copyOf()
        pinSalt = null
        pinProtected = false
    }

    override suspend fun isPinProtected(): Boolean = mutex.withLock { pinProtected }

    override suspend fun getBiometricWrappedKey(): ByteArray? = mutex.withLock { biometricWrappedKey?.copyOf() }

    override suspend fun setBiometricWrappedKey(wrapped: ByteArray?) = mutex.withLock {
        biometricWrappedKey = wrapped?.copyOf()
    }

    override suspend fun insertRecord(record: FinanceRecord) = mutex.withLock { records.add(0, record) }

    override suspend fun updateRecord(record: FinanceRecord) = mutex.withLock {
        val index = records.indexOfFirst { it.id == record.id }
        if (index >= 0) records[index] = record
    }

    override suspend fun deleteRecord(id: String) = mutex.withLock {
        records.removeAll { it.id == id }
        Unit
    }

    override suspend fun allRecords(): List<FinanceRecord> = mutex.withLock { records.toList() }

    override suspend fun insertEvent(event: Event) = mutex.withLock { events.add(0, event) }

    override suspend fun updateEvent(event: Event) = mutex.withLock {
        val index = events.indexOfFirst { it.id == event.id }
        if (index >= 0) events[index] = event
    }

    override suspend fun deleteEvent(id: String) = mutex.withLock {
        events.removeAll { it.id == id }
        Unit
    }

    override suspend fun allEvents(): List<Event> = mutex.withLock { events.toList() }

    override suspend fun insertDebt(debt: Debt) = mutex.withLock { debts.add(0, debt) }

    override suspend fun updateDebt(debt: Debt) = mutex.withLock {
        val index = debts.indexOfFirst { it.id == debt.id }
        if (index >= 0) debts[index] = debt
    }

    override suspend fun deleteDebt(id: String) = mutex.withLock {
        debts.removeAll { it.id == id }
        Unit
    }

    override suspend fun allDebts(): List<Debt> = mutex.withLock { debts.toList() }

    override suspend fun insertSharedCost(cost: SharedCost) = mutex.withLock { sharedCosts.add(0, cost) }

    override suspend fun allSharedCosts(): List<SharedCost> = mutex.withLock { sharedCosts.toList() }

    override suspend fun clearAll() = mutex.withLock {
        records.clear()
        events.clear()
        debts.clear()
        sharedCosts.clear()
        monthlyBudgetCents = null
        failedAttemptCount = 0
        lockoutUntilEpochMillis = null
        securityAnswerHash = null
        securityQuestionId = null
        biometricEnrolled = false
        biometricWrappedKey = null
        activeKey = null
        pinSalt = null
        pinProtected = false
    }
}
