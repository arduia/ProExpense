package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordChecksum
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.WalletId
import com.arduia.expense.storage.db.Finance_record as FinanceRecordRow

/**
 * Row tag columns encode the polymorphic [RecordLink]. Kept as constants so read and write stay in
 * sync — a typo here would silently orphan links.
 */
internal object RecordLinkTag {
    const val EVENT = "EVENT"
    const val DEBT = "DEBT"
    const val SHARED_COST = "SHARED_COST"
}

internal fun FinanceRecordRow.toDomain(): FinanceRecord =
    FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(amount_cents), CurrencyCode(currency_code)),
        homeCurrencyMoney =
            Money(
                Amount(home_amount_cents ?: amount_cents),
                CurrencyCode(home_currency_code ?: currency_code),
            ),
        categoryId = CategoryId(category_id),
        type = type.toRecordTypeFromCode(),
        note = note,
        recordedAtEpochMillis = recorded_at,
        link = toRecordLink(tag_type, tag_id),
        integrity = toChecksum(integrity_algo, integrity_hash),
        walletId = wallet_id?.let { WalletId(it.toInt()) },
    )

internal fun toChecksum(
    algorithm: String?,
    value: String?,
): RecordChecksum? = if (algorithm != null && value != null) RecordChecksum(algorithm, value) else null

internal fun toRecordLink(
    tagType: String?,
    tagId: String?,
): RecordLink =
    when (tagType) {
        null -> RecordLink.None
        RecordLinkTag.EVENT -> RecordLink.ToEvent(EventId(requireTagId(tagId, tagType)))
        RecordLinkTag.DEBT -> RecordLink.ToDebt(DebtId(requireTagId(tagId, tagType)))
        RecordLinkTag.SHARED_COST -> RecordLink.ToSharedCost(SharedCostId(requireTagId(tagId, tagType)))
        else -> error("Unknown tag_type: $tagType")
    }

internal fun RecordLink.tagType(): String? =
    when (this) {
        RecordLink.None -> null
        is RecordLink.ToEvent -> RecordLinkTag.EVENT
        is RecordLink.ToDebt -> RecordLinkTag.DEBT
        is RecordLink.ToSharedCost -> RecordLinkTag.SHARED_COST
    }

internal fun RecordLink.tagId(): String? =
    when (this) {
        RecordLink.None -> null
        is RecordLink.ToEvent -> eventId.value
        is RecordLink.ToDebt -> debtId.value
        is RecordLink.ToSharedCost -> sharedCostId.value
    }

private fun requireTagId(
    tagId: String?,
    tagType: String,
): String = requireNotNull(tagId) { "tag_id must be present when tag_type is $tagType" }

/**
 * Enum codec for RecordType — explicit stable codes (EXPENSE=0, INCOME=1).
 * Never use Kotlin .ordinal as that changes when enum members are reordered.
 */
internal fun Long.toRecordTypeFromCode(): RecordType =
    when (this) {
        0L -> RecordType.EXPENSE
        1L -> RecordType.INCOME
        else -> error("Unknown RecordType code: $this")
    }

internal fun RecordType.toCode(): Long =
    when (this) {
        RecordType.EXPENSE -> 0L
        RecordType.INCOME -> 1L
    }
