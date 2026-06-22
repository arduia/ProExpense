package com.arduia.expense.feature.logging

import com.arduia.expense.domain.ExpenseTagType

/**
 * Carries an entered/edited expense across the feature → app boundary in both directions:
 * the app passes one in to pre-fill an edit, and receives one back on save.
 *
 * [id] is the record identity — null means "create a new record", non-null means "update this one".
 * [recordedAtEpochMillis] is null for a fresh entry (the app stamps "now") and preserved on edit.
 */
data class LoggedExpenseHandoff(
    val id: String? = null,
    val categoryId: String,
    val note: String,
    val rawAmount: String,
    val currencyCode: String = "USD",
    val recordedAtEpochMillis: Long? = null,
    val timeLabel: String,
    val tagType: ExpenseTagType? = null,
    val tagId: String? = null,
    val linkedTagLabel: String? = null,
)
