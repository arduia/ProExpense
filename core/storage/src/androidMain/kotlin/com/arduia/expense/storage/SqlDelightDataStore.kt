package com.arduia.expense.storage

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightDataStore(
    private val database: ProExpenseDatabase,
) : EntityDataStore {
    override var monthlyBudgetCents: Long? = null
    override var homeCurrencyCode: String = "USD"
    override var failedAttemptCount: Int = 0
    override var lockoutUntilEpochMillis: Long? = null
    override var securityAnswerHash: String? = null
    override var securityQuestionId: String? = null
    override var biometricEnrolled: Boolean = false

    private var biometricWrappedKey: ByteArray? = null

    suspend fun bootstrapFromMeta() = withContext(Dispatchers.IO) {
        val meta = database.appMetaQueries.selectMeta().executeAsOneOrNull()
        if (meta == null) {
            persistMeta()
            return@withContext
        }
        monthlyBudgetCents = meta.monthly_budget_cents
        homeCurrencyCode = meta.home_currency_code
        failedAttemptCount = meta.failed_attempt_count.toInt()
        lockoutUntilEpochMillis = meta.lockout_until
        securityAnswerHash = meta.security_answer_hash
        securityQuestionId = meta.security_question_id
        biometricEnrolled = meta.biometric_enrolled == 1L
        biometricWrappedKey = meta.biometric_wrapped_key
    }

    override suspend fun getBiometricWrappedKey(): ByteArray? = withContext(Dispatchers.IO) {
        biometricWrappedKey?.copyOf()
    }

    override suspend fun setBiometricWrappedKey(wrapped: ByteArray?) = withContext(Dispatchers.IO) {
        biometricWrappedKey = wrapped?.copyOf()
        persistMeta()
    }

    override suspend fun insertRecord(record: FinanceRecord) = withContext(Dispatchers.IO) {
        database.financeRecordQueries.insertRecord(
            id = record.id,
            amount_cents = record.amount.valueInCents,
            currency_code = record.currency.code,
            home_amount_cents = record.homeCurrencyAmount.valueInCents,
            category_id = record.categoryId,
            type = record.type.name,
            note = record.note,
            recorded_at = record.recordedAtEpochMillis,
            tag_type = record.tagType?.name,
            tag_id = record.tagId,
        )
        Unit
    }

    override suspend fun updateRecord(record: FinanceRecord) {
        insertRecord(record)
    }

    override suspend fun deleteRecord(id: String) = withContext(Dispatchers.IO) {
        database.financeRecordQueries.deleteRecord(id)
        Unit
    }

    override suspend fun allRecords(): List<FinanceRecord> = withContext(Dispatchers.IO) {
        database.financeRecordQueries.selectAllRecords().executeAsList().map(SqlDelightEntityMapper::toFinanceRecord)
    }

    override suspend fun insertEvent(event: Event) = withContext(Dispatchers.IO) {
        database.eventQueries.insertEvent(
            id = event.id,
            name = event.name,
            start_epoch_millis = event.startEpochMillis,
            end_epoch_millis = event.endEpochMillis,
            budget_cents = event.budgetAmount.valueInCents,
            status = event.status.name,
        )
        Unit
    }

    override suspend fun updateEvent(event: Event) {
        insertEvent(event)
    }

    override suspend fun deleteEvent(id: String) = withContext(Dispatchers.IO) {
        database.eventQueries.deleteEvent(id)
        Unit
    }

    override suspend fun allEvents(): List<Event> = withContext(Dispatchers.IO) {
        database.eventQueries.selectAllEvents().executeAsList().map(SqlDelightEntityMapper::toEvent)
    }

    override suspend fun insertDebt(debt: Debt) = withContext(Dispatchers.IO) {
        database.debtQueries.insertDebt(
            id = debt.id,
            person_name = debt.personName,
            amount_cents = debt.amount.valueInCents,
            direction = debt.direction.name,
            due_epoch_millis = debt.dueEpochMillis,
            is_settled = if (debt.isSettled) 1L else 0L,
        )
        Unit
    }

    override suspend fun updateDebt(debt: Debt) {
        insertDebt(debt)
    }

    override suspend fun deleteDebt(id: String) = withContext(Dispatchers.IO) {
        database.debtQueries.deleteDebt(id)
        Unit
    }

    override suspend fun allDebts(): List<Debt> = withContext(Dispatchers.IO) {
        database.debtQueries.selectAllDebts().executeAsList().map(SqlDelightEntityMapper::toDebt)
    }

    override suspend fun insertSharedCost(cost: SharedCost) = withContext(Dispatchers.IO) {
        database.sharedCostQueries.insertSharedCost(
            id = cost.id,
            title = cost.title,
            total_cents = cost.totalAmount.valueInCents,
            currency_code = cost.currency.code,
            recorded_at = cost.recordedAtEpochMillis,
            participants_json = SharedCostJson.encodeParticipants(cost.participants),
            custom_shares_json = cost.customShareCents?.let { SharedCostJson.encodeLongList(it) },
        )
        Unit
    }

    override suspend fun allSharedCosts(): List<SharedCost> = withContext(Dispatchers.IO) {
        database.sharedCostQueries.selectAllSharedCosts().executeAsList().map(SqlDelightEntityMapper::toSharedCost)
    }

    override suspend fun clearAll() = withContext(Dispatchers.IO) {
        database.financeRecordQueries.deleteAllRecords()
        database.eventQueries.deleteAllEvents()
        database.debtQueries.deleteAllDebts()
        database.sharedCostQueries.deleteAllSharedCosts()
        monthlyBudgetCents = null
        failedAttemptCount = 0
        lockoutUntilEpochMillis = null
        securityAnswerHash = null
        securityQuestionId = null
        biometricEnrolled = false
        biometricWrappedKey = null
        persistMeta()
    }

    override suspend fun persistSettings() = withContext(Dispatchers.IO) {
        persistMeta()
    }

    private fun persistMeta() {
        database.appMetaQueries.insertMeta(
            id = 1L,
            monthly_budget_cents = monthlyBudgetCents,
            home_currency_code = homeCurrencyCode,
            failed_attempt_count = failedAttemptCount.toLong(),
            lockout_until = lockoutUntilEpochMillis,
            security_answer_hash = securityAnswerHash,
            security_question_id = securityQuestionId,
            biometric_enrolled = if (biometricEnrolled) 1L else 0L,
            biometric_wrapped_key = biometricWrappedKey,
        )
    }
}
