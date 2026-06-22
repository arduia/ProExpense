package com.arduia.expense.ui.logging

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.storage.db.DefaultCategories
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.format.DateLabels
import kotlin.math.roundToLong

/**
 * Write-model for Add Expense — create, edit, and update — translating between the feature's
 * [LoggedExpenseHandoff] and the persisted [FinanceRecord]. Single-currency MVP: the original and
 * home amounts are the same and stamped with the profile's home currency.
 */
class ExpenseEntryViewModel(
    private val financeRepository: FinanceRecordRepository,
    private val profileRepository: ProfileRepository,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val idGenerator: () -> String = { java.util.UUID.randomUUID().toString() },
) {

    /** A blank entry stamped with the home currency + current time (the "+" Add path). */
    suspend fun newEntryStart(): LoggedExpenseHandoff {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val now = clock()
        return LoggedExpenseHandoff(
            id = null,
            categoryId = DefaultCategories.FOOD_ID,
            note = "",
            rawAmount = "",
            currencyCode = currency,
            recordedAtEpochMillis = null,
            timeLabel = DateLabels.timeLabel(now),
        )
    }

    /** Loads an existing record into an editable handoff, or null if it no longer exists. */
    suspend fun loadForEdit(id: String): LoggedExpenseHandoff? {
        val record = financeRepository.getById(id).getOrNull() ?: return null
        return LoggedExpenseHandoff(
            id = record.id,
            categoryId = record.categoryId,
            note = record.note.orEmpty(),
            rawAmount = rawFromCents(record.amount.valueInCents),
            currencyCode = record.currency.code,
            recordedAtEpochMillis = record.recordedAtEpochMillis,
            timeLabel = DateLabels.timeLabel(record.recordedAtEpochMillis),
            tagType = record.tagType,
            tagId = record.tagId,
        )
    }

    /** Persists a create (id == null) or update (id != null). */
    suspend fun submit(handoff: LoggedExpenseHandoff): Result<Unit> {
        val cents = centsFrom(handoff.rawAmount)
        val currency = parseCurrency(handoff.currencyCode)
        val record = FinanceRecord(
            id = handoff.id ?: idGenerator(),
            amount = Amount(cents),
            currency = currency,
            homeCurrencyAmount = Amount(cents),
            categoryId = handoff.categoryId,
            type = RecordType.EXPENSE,
            note = handoff.note.takeIf { it.isNotBlank() },
            recordedAtEpochMillis = handoff.recordedAtEpochMillis ?: clock(),
            tagType = handoff.tagType,
            tagId = handoff.tagId,
        )
        return financeRepository.upsert(record)
    }

    private fun centsFrom(rawAmount: String): Long {
        val dollars = AmountInput.numericValue(rawAmount) ?: 0.0
        return (dollars * 100).roundToLong().coerceIn(0L, Amount.MAX_VALUE_IN_CENTS)
    }

    private fun rawFromCents(cents: Long): String {
        val whole = cents / 100
        val fraction = (cents % 100).toInt()
        return "$whole.${fraction.toString().padStart(2, '0')}"
    }

    private fun parseCurrency(code: String): CurrencyCode = try {
        CurrencyCode(code)
    } catch (e: IllegalArgumentException) {
        CurrencyCode("USD")
    }
}
