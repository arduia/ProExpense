package com.arduia.expense.feature.logging

enum class TagOptionKind { EVENT, DEBT }

/**
 * Plain tag-link choice for the expense entry tag picker. Carries raw fields only — date/currency
 * display formatting is a presentation concern handled by the platform UI layer.
 */
data class TagOption(
    val id: String,
    val kind: TagOptionKind,
    val eventName: String? = null,
    val eventStartEpochMillis: Long? = null,
    val eventEndEpochMillis: Long? = null,
    val eventIsClosed: Boolean = false,
    val debtPersonName: String? = null,
    val debtIsOwedToMe: Boolean? = null,
    val debtAmountCents: Long? = null,
)
