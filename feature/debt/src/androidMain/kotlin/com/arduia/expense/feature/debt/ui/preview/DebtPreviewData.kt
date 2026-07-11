package com.arduia.expense.feature.debt.ui.preview

import com.arduia.expense.ui.design.PlatformDateFormatter

const val DEBT_PERSON_MAX = 30
const val DEBT_NOTE_MAX = 200

enum class DebtSide {
    Lent,
    Owe,
}

data class DebtRecordUi(
    val id: String,
    val name: String,
    val dateLabel: String,
    val amountLabel: String,
    val subtitle: String? = null,
    val settled: Boolean = false,
    val amountCents: Long = 0,
    val dueEpochMillis: Long? = null,
    /** When this record was created — distinct from [dueEpochMillis], which is an optional
     * reference-only due date. Used for Detail's "Date Recorded" label, not [dateLabel] (which is
     * actually the due-date/"No due date" summary shown in the list row). */
    val recordedAtEpochMillis: Long = 0L,
    val recordAsTransaction: Boolean = false,
) {
    val initial: String =
        name
            .trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString() ?: "?"
}

data class DebtListUiState(
    val side: DebtSide,
    val netLabel: String,
    val activeCount: Int,
    val active: List<DebtRecordUi>,
    val settled: List<DebtRecordUi>,
    val isLoading: Boolean = false,
)

data class DebtLinkedExpenseUi(
    val id: String,
    val categoryId: String,
    val title: String,
    val amountLabel: String,
)

data class DebtDetailUiState(
    val id: String,
    val side: DebtSide,
    val name: String,
    val amountLabel: String,
    val dateRecordedLabel: String,
    val dueLabel: String,
    val statusLabel: String,
    val settled: Boolean,
    val note: String? = null,
    val linkedExpense: DebtLinkedExpenseUi? = null,
) {
    val initial: String =
        name
            .trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString() ?: "?"
}

data class DebtAddFormState(
    val side: DebtSide = DebtSide.Lent,
    val person: String = "",
    val amountRaw: String = "",
    // A new record is recorded today, not a hardcoded placeholder month.
    val dateLabel: String = PlatformDateFormatter.shortDateLabel(System.currentTimeMillis()),
    val dueLabel: String? = null,
    val editingId: String? = null,
    val dueEpochMillis: Long? = null,
    val note: String = "",
    val recordAsTransaction: Boolean = false,
) {
    val canSave: Boolean
        get() = person.isNotBlank() && (amountRaw.toDoubleOrNull() ?: 0.0) > 0.0
}

val previewDebtLent =
    DebtListUiState(
        side = DebtSide.Lent,
        netLabel = "$135",
        activeCount = 3,
        active =
            listOf(
                DebtRecordUi("john", "John", "May 12", "$50", subtitle = "dinner at Nobu"),
                DebtRecordUi("maya", "Maya", "May 08", "$25", subtitle = "due May 30"),
                DebtRecordUi("sarah", "Sarah", "Apr 28", "$60"),
            ),
        settled =
            listOf(
                DebtRecordUi("aiko", "Aiko", "Apr 14", "$20", settled = true),
            ),
    )

val previewDebtLoading =
    DebtListUiState(
        side = DebtSide.Lent,
        netLabel = "$0",
        activeCount = 0,
        active = emptyList(),
        settled = emptyList(),
        isLoading = true,
    )

val previewDebtOwe =
    DebtListUiState(
        side = DebtSide.Owe,
        netLabel = "$45",
        activeCount = 2,
        active =
            listOf(
                DebtRecordUi("david", "David", "May 14", "$30", subtitle = "taxi share"),
                DebtRecordUi("lin", "Lin", "May 02", "$15"),
            ),
        settled = emptyList(),
    )

val previewDebtSettled =
    DebtListUiState(
        side = DebtSide.Lent,
        netLabel = "$0",
        activeCount = 0,
        active = emptyList(),
        settled =
            listOf(
                DebtRecordUi("aiko", "Aiko", "Apr 14", "$20", settled = true),
                DebtRecordUi("liam", "Liam", "Apr 02", "$15", settled = true),
            ),
    )

val previewDebtLentDetail =
    DebtDetailUiState(
        id = "john",
        side = DebtSide.Lent,
        name = "John",
        amountLabel = "$50",
        dateRecordedLabel = "May 12, 2026",
        dueLabel = "May 30, 2026",
        statusLabel = "Active",
        settled = false,
        note = "Dinner at Nobu — covered his share.",
        linkedExpense =
            DebtLinkedExpenseUi(
                id = "exp-1",
                categoryId = "food",
                title = "Dinner · seafood",
                amountLabel = "$64",
            ),
    )

val previewDebtOweDetail =
    DebtDetailUiState(
        id = "david",
        side = DebtSide.Owe,
        name = "David",
        amountLabel = "$30",
        dateRecordedLabel = "May 14, 2026",
        dueLabel = "No due date",
        statusLabel = "Active",
        settled = false,
        note = "Taxi share back from the airport.",
    )

val previewDebtAddLent =
    DebtAddFormState(
        side = DebtSide.Lent,
        person = "John",
        amountRaw = "50",
        dateLabel = "May 12",
        dueLabel = null,
    )
