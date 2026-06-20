package com.arduia.expense.storage

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost

internal data class StoreSnapshot(
    val records: List<FinanceRecord>,
    val events: List<Event>,
    val debts: List<Debt>,
    val sharedCosts: List<SharedCost>,
    val monthlyBudgetCents: Long?,
    val homeCurrencyCode: String,
    val failedAttemptCount: Int,
    val lockoutUntilEpochMillis: Long?,
    val securityAnswerNormalized: String?,
    val activeKey: ByteArray?,
    val pinSalt: ByteArray?,
    val pinProtected: Boolean,
)

internal object StoreSnapshotCodec {
    fun encode(snapshot: StoreSnapshot): ByteArray = buildString {
        append("R")
        snapshot.records.forEach { record ->
            append("|")
            append(record.id)
            append(";")
            append(record.homeCurrencyAmount.valueInCents)
            append(";")
            append(record.categoryId)
            append(";")
            append(record.note.orEmpty())
            append(";")
            append(record.recordedAtEpochMillis)
            append(";")
            append(record.tagType?.name.orEmpty())
            append(";")
            append(record.tagId.orEmpty())
        }
        append("#E")
        snapshot.events.forEach { event ->
            append("|")
            append(event.id)
            append(";")
            append(event.name)
            append(";")
            append(event.startEpochMillis)
            append(";")
            append(event.endEpochMillis)
            append(";")
            append(event.budgetAmount.valueInCents)
            append(";")
            append(event.status.name)
        }
        append("#D")
        snapshot.debts.forEach { debt ->
            append("|")
            append(debt.id)
            append(";")
            append(debt.personName)
            append(";")
            append(debt.amount.valueInCents)
            append(";")
            append(debt.direction.name)
            append(";")
            append(debt.isSettled)
        }
        append("#S")
        snapshot.sharedCosts.forEach { cost ->
            append("|")
            append(cost.id)
            append(";")
            append(cost.title)
            append(";")
            append(cost.totalAmount.valueInCents)
            append(";")
            append(cost.currency.code)
            append(";")
            append(cost.recordedAtEpochMillis)
            append(";")
            append(cost.participants.size)
        }
        append("#M")
        append(snapshot.monthlyBudgetCents ?: -1)
        append(";")
        append(snapshot.homeCurrencyCode)
        append(";")
        append(snapshot.failedAttemptCount)
        append(";")
        append(snapshot.lockoutUntilEpochMillis ?: -1)
        append(";")
        append(snapshot.securityAnswerNormalized.orEmpty())
        append(";")
        append(snapshot.pinProtected)
        append(";")
        append(snapshot.activeKey?.let { encodeBytes(it) }.orEmpty())
        append(";")
        append(snapshot.pinSalt?.let { encodeBytes(it) }.orEmpty())
    }.encodeToByteArray()

    fun decode(payload: ByteArray): StoreSnapshot {
        val text = payload.decodeToString()
        val recordsPart = text.substringAfter("R").substringBefore("#E")
        val eventsPart = text.substringAfter("#E").substringBefore("#D")
        val debtsPart = text.substringAfter("#D").substringBefore("#S")
        val sharedPart = text.substringAfter("#S").substringBefore("#M")
        val metaPart = text.substringAfter("#M")

        val records = recordsPart.split("|").filter { it.isNotBlank() }.mapNotNull { chunk ->
            val p = chunk.split(";")
            if (p.size < 5) return@mapNotNull null
            val tagType = p.getOrNull(5)?.takeIf { it.isNotBlank() }
            val tagId = p.getOrNull(6)?.takeIf { it.isNotBlank() }
            FinanceRecord(
                id = p[0],
                amount = Amount(p[1].toLong()),
                currency = CurrencyCode("USD"),
                homeCurrencyAmount = Amount(p[1].toLong()),
                categoryId = p[2],
                type = RecordType.EXPENSE,
                note = p[3].takeIf { it.isNotEmpty() },
                recordedAtEpochMillis = p[4].toLong(),
                tagType = tagType?.let { com.arduia.expense.domain.ExpenseTagType.valueOf(it) },
                tagId = tagId,
            )
        }

        val events = eventsPart.split("|").filter { it.isNotBlank() }.mapNotNull { chunk ->
            val p = chunk.split(";")
            if (p.size < 6) return@mapNotNull null
            Event(
                id = p[0],
                name = p[1],
                startEpochMillis = p[2].toLong(),
                endEpochMillis = p[3].toLong(),
                budgetAmount = Amount(p[4].toLong()),
                status = EventStatus.valueOf(p[5]),
            )
        }

        val debts = debtsPart.split("|").filter { it.isNotBlank() }.mapNotNull { chunk ->
            val p = chunk.split(";")
            if (p.size < 5) return@mapNotNull null
            Debt(
                id = p[0],
                personName = p[1],
                amount = Amount(p[2].toLong()),
                direction = DebtDirection.valueOf(p[3]),
                isSettled = p[4].toBoolean(),
            )
        }

        val sharedCosts = sharedPart.split("|").filter { it.isNotBlank() }.mapNotNull { chunk ->
            val p = chunk.split(";")
            if (p.size < 6) return@mapNotNull null
            val count = p[5].toInt()
            SharedCost(
                id = p[0],
                title = p[1],
                totalAmount = Amount(p[2].toLong()),
                currency = CurrencyCode(p[3]),
                participants = List(count) { index ->
                    com.arduia.expense.domain.Participant(id = "p$index", name = "Person ${index + 1}")
                },
                recordedAtEpochMillis = p[4].toLong(),
            )
        }

        val meta = metaPart.split(";")
        return StoreSnapshot(
            records = records,
            events = events,
            debts = debts,
            sharedCosts = sharedCosts,
            monthlyBudgetCents = meta.getOrNull(0)?.toLong()?.takeIf { it >= 0 },
            homeCurrencyCode = meta.getOrNull(1) ?: "USD",
            failedAttemptCount = meta.getOrNull(2)?.toIntOrNull() ?: 0,
            lockoutUntilEpochMillis = meta.getOrNull(3)?.toLong()?.takeIf { it >= 0 },
            securityAnswerNormalized = meta.getOrNull(4)?.takeIf { it.isNotEmpty() },
            activeKey = meta.getOrNull(6)?.takeIf { it.isNotEmpty() }?.let { decodeBytes(it) },
            pinSalt = meta.getOrNull(7)?.takeIf { it.isNotEmpty() }?.let { decodeBytes(it) },
            pinProtected = meta.getOrNull(5)?.toBooleanStrictOrNull() ?: false,
        )
    }

    private fun encodeBytes(bytes: ByteArray): String =
        java.util.Base64.getEncoder().encodeToString(bytes)

    private fun decodeBytes(encoded: String): ByteArray =
        java.util.Base64.getDecoder().decode(encoded)
}
