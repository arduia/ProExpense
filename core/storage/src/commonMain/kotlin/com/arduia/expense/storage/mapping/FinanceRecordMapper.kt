package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCostId
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
        homeCurrencyMoney = Money(Amount(home_amount_cents), CurrencyCode(currency_code)),
        categoryId = CategoryId(category_id),
        type = RecordType.valueOf(type),
        note = note,
        recordedAtEpochMillis = recorded_at,
        link = toRecordLink(tag_type, tag_id),
    )

internal fun toRecordLink(tagType: String?, tagId: String?): RecordLink = when (tagType) {
    null -> RecordLink.None
    RecordLinkTag.EVENT -> RecordLink.ToEvent(EventId(requireTagId(tagId, tagType)))
    RecordLinkTag.DEBT -> RecordLink.ToDebt(DebtId(requireTagId(tagId, tagType)))
    RecordLinkTag.SHARED_COST -> RecordLink.ToSharedCost(SharedCostId(requireTagId(tagId, tagType)))
    else -> error("Unknown tag_type: $tagType")
}

internal fun RecordLink.tagType(): String? = when (this) {
    RecordLink.None -> null
    is RecordLink.ToEvent -> RecordLinkTag.EVENT
    is RecordLink.ToDebt -> RecordLinkTag.DEBT
    is RecordLink.ToSharedCost -> RecordLinkTag.SHARED_COST
}

internal fun RecordLink.tagId(): String? = when (this) {
    RecordLink.None -> null
    is RecordLink.ToEvent -> eventId.value
    is RecordLink.ToDebt -> debtId.value
    is RecordLink.ToSharedCost -> sharedCostId.value
}

private fun requireTagId(tagId: String?, tagType: String): String =
    requireNotNull(tagId) { "tag_id must be present when tag_type is $tagType" }
