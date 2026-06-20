package com.arduia.expense.storage

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost

interface EntityDataStore {
    var monthlyBudgetCents: Long?
    var homeCurrencyCode: String
    var failedAttemptCount: Int
    var lockoutUntilEpochMillis: Long?
    var securityAnswerHash: String?
    var securityQuestionId: String?
    var biometricEnrolled: Boolean

    suspend fun persistSettings()

    suspend fun insertRecord(record: FinanceRecord)

    suspend fun updateRecord(record: FinanceRecord)

    suspend fun deleteRecord(id: String)

    suspend fun allRecords(): List<FinanceRecord>

    suspend fun insertEvent(event: Event)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(id: String)

    suspend fun allEvents(): List<Event>

    suspend fun insertDebt(debt: Debt)

    suspend fun updateDebt(debt: Debt)

    suspend fun deleteDebt(id: String)

    suspend fun allDebts(): List<Debt>

    suspend fun insertSharedCost(cost: SharedCost)

    suspend fun allSharedCosts(): List<SharedCost>

    suspend fun getBiometricWrappedKey(): ByteArray?

    suspend fun setBiometricWrappedKey(wrapped: ByteArray?)

    suspend fun clearAll()
}
