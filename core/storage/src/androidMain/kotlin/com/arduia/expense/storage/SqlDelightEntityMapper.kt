package com.arduia.expense.storage

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.storage.db.Finance_record
import com.arduia.expense.storage.db.Event_record
import com.arduia.expense.storage.db.Debt_record
import com.arduia.expense.storage.db.Shared_cost

internal object SqlDelightEntityMapper {
    fun toFinanceRecord(row: Finance_record): FinanceRecord = FinanceRecord(
        id = row.id,
        amount = Amount(row.amount_cents),
        currency = CurrencyCode(row.currency_code),
        homeCurrencyAmount = Amount(row.home_amount_cents),
        categoryId = row.category_id,
        type = RecordType.valueOf(row.type),
        note = row.note,
        recordedAtEpochMillis = row.recorded_at,
        tagType = row.tag_type?.let { ExpenseTagType.valueOf(it) },
        tagId = row.tag_id,
    )

    fun toEvent(row: Event_record): Event = Event(
        id = row.id,
        name = row.name,
        startEpochMillis = row.start_epoch_millis,
        endEpochMillis = row.end_epoch_millis,
        budgetAmount = Amount(row.budget_cents),
        status = EventStatus.valueOf(row.status),
    )

    fun toDebt(row: Debt_record): Debt = Debt(
        id = row.id,
        personName = row.person_name,
        amount = Amount(row.amount_cents),
        direction = DebtDirection.valueOf(row.direction),
        dueEpochMillis = row.due_epoch_millis,
        isSettled = row.is_settled == 1L,
    )

    fun toSharedCost(row: Shared_cost): SharedCost {
        val participants = SharedCostJson.decodeParticipants(row.participants_json)
        val customShares = row.custom_shares_json?.let { SharedCostJson.decodeLongList(it) }
        return SharedCost(
            id = row.id,
            title = row.title,
            totalAmount = Amount(row.total_cents),
            currency = CurrencyCode(row.currency_code),
            participants = participants,
            recordedAtEpochMillis = row.recorded_at,
            customShareCents = customShares,
        )
    }
}

internal object SharedCostJson {
    fun encodeParticipants(participants: List<Participant>): String =
        participants.joinToString("|") { "${it.id};${it.name}" }

    fun decodeParticipants(payload: String): List<Participant> =
        if (payload.isBlank()) emptyList() else payload.split("|").mapNotNull { chunk ->
            val parts = chunk.split(";")
            if (parts.size < 2) return@mapNotNull null
            Participant(id = parts[0], name = parts[1])
        }

    fun encodeLongList(values: List<Long>): String = values.joinToString(",")

    fun decodeLongList(payload: String): List<Long> =
        if (payload.isBlank()) emptyList() else payload.split(",").mapNotNull { it.toLongOrNull() }
}
