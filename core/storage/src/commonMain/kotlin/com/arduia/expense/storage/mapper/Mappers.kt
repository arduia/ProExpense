package com.arduia.expense.storage.mapper

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.storage.db.Category_record
import com.arduia.expense.storage.db.Debt_record
import com.arduia.expense.storage.db.Event_record
import com.arduia.expense.storage.db.Finance_record

// Booleans are stored as 0/1 INTEGER columns (SQLite has no native boolean type).
internal fun Boolean.toDb(): Long = if (this) 1L else 0L

internal fun Long.toBoolean(): Boolean = this != 0L

internal fun Finance_record.toDomain(): FinanceRecord = FinanceRecord(
    id = id,
    amount = Amount(amount_cents),
    currency = CurrencyCode(currency_code),
    homeCurrencyAmount = Amount(home_amount_cents),
    categoryId = category_id,
    type = RecordType.valueOf(type),
    note = note,
    recordedAtEpochMillis = recorded_at,
    tagType = tag_type?.let { ExpenseTagType.valueOf(it) },
    tagId = tag_id,
)

internal fun Event_record.toDomain(): Event = Event(
    id = id,
    name = name,
    startEpochMillis = start_epoch_millis,
    endEpochMillis = end_epoch_millis,
    budgetAmount = Amount(budget_cents),
    status = EventStatus.valueOf(status),
)

internal fun Debt_record.toDomain(): Debt = Debt(
    id = id,
    personName = person_name,
    amount = Amount(amount_cents),
    direction = DebtDirection.valueOf(direction),
    dueEpochMillis = due_epoch_millis,
    isSettled = is_settled.toBoolean(),
)

internal fun Category_record.toDomain(): Category = Category(
    id = id,
    name = name,
    isCustom = is_custom.toBoolean(),
)
