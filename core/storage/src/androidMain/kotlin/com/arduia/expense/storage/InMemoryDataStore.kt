package com.arduia.expense.storage

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDataStore : KeyRotationStore {
    private val mutex = Mutex()
    private val records = mutableListOf<FinanceRecord>()
    private val events = mutableListOf<Event>()
    private val debts = mutableListOf<Debt>()
    private val sharedCosts = mutableListOf<SharedCost>()

    var monthlyBudgetCents: Long? = null
    var homeCurrencyCode: String = "USD"
    var failedAttemptCount: Int = 0
    var lockoutUntilEpochMillis: Long? = null
    var securityAnswerNormalized: String? = null

    private var activeKey: ByteArray? = null
    private var pinSalt: ByteArray? = null
    private var pinProtected: Boolean = false

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

    suspend fun insertRecord(record: FinanceRecord) = mutex.withLock { records.add(0, record) }

    suspend fun updateRecord(record: FinanceRecord) = mutex.withLock {
        val index = records.indexOfFirst { it.id == record.id }
        if (index >= 0) records[index] = record
    }

    suspend fun deleteRecord(id: String) = mutex.withLock {
        records.removeAll { it.id == id }
    }

    suspend fun allRecords(): List<FinanceRecord> = mutex.withLock { records.toList() }

    suspend fun clearRecords() = mutex.withLock { records.clear() }

    suspend fun insertEvent(event: Event) = mutex.withLock { events.add(0, event) }

    suspend fun updateEvent(event: Event) = mutex.withLock {
        val index = events.indexOfFirst { it.id == event.id }
        if (index >= 0) events[index] = event
    }

    suspend fun deleteEvent(id: String) = mutex.withLock { events.removeAll { it.id == id } }

    suspend fun allEvents(): List<Event> = mutex.withLock { events.toList() }

    suspend fun insertDebt(debt: Debt) = mutex.withLock { debts.add(0, debt) }

    suspend fun updateDebt(debt: Debt) = mutex.withLock {
        val index = debts.indexOfFirst { it.id == debt.id }
        if (index >= 0) debts[index] = debt
    }

    suspend fun deleteDebt(id: String) = mutex.withLock { debts.removeAll { it.id == id } }

    suspend fun allDebts(): List<Debt> = mutex.withLock { debts.toList() }

    suspend fun insertSharedCost(cost: SharedCost) = mutex.withLock { sharedCosts.add(0, cost) }

    suspend fun allSharedCosts(): List<SharedCost> = mutex.withLock { sharedCosts.toList() }

    suspend fun clearSharedCosts() = mutex.withLock { sharedCosts.clear() }

    suspend fun clearAll() = mutex.withLock {
        records.clear()
        events.clear()
        debts.clear()
        sharedCosts.clear()
        monthlyBudgetCents = null
        failedAttemptCount = 0
        lockoutUntilEpochMillis = null
        securityAnswerNormalized = null
        activeKey = null
        pinSalt = null
        pinProtected = false
    }

    internal suspend fun exportSnapshot(): StoreSnapshot = mutex.withLock {
        StoreSnapshot(
            records = records.toList(),
            events = events.toList(),
            debts = debts.toList(),
            sharedCosts = sharedCosts.toList(),
            monthlyBudgetCents = monthlyBudgetCents,
            homeCurrencyCode = homeCurrencyCode,
            failedAttemptCount = failedAttemptCount,
            lockoutUntilEpochMillis = lockoutUntilEpochMillis,
            securityAnswerNormalized = securityAnswerNormalized,
            activeKey = activeKey?.copyOf(),
            pinSalt = pinSalt?.copyOf(),
            pinProtected = pinProtected,
        )
    }

    internal suspend fun importSnapshot(snapshot: StoreSnapshot) = mutex.withLock {
        records.clear()
        records.addAll(snapshot.records)
        events.clear()
        events.addAll(snapshot.events)
        debts.clear()
        debts.addAll(snapshot.debts)
        sharedCosts.clear()
        sharedCosts.addAll(snapshot.sharedCosts)
        monthlyBudgetCents = snapshot.monthlyBudgetCents
        homeCurrencyCode = snapshot.homeCurrencyCode
        failedAttemptCount = snapshot.failedAttemptCount
        lockoutUntilEpochMillis = snapshot.lockoutUntilEpochMillis
        securityAnswerNormalized = snapshot.securityAnswerNormalized
        activeKey = snapshot.activeKey?.copyOf()
        pinSalt = snapshot.pinSalt?.copyOf()
        pinProtected = snapshot.pinProtected
    }
}
