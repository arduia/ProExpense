package com.arduia.expense.feature.history.ui.preview

import com.arduia.expense.ui.design.ProTransactionRowModel

data class JournalDayUi(
    val id: String,
    val title: String,
    val total: String,
    val rows: List<ProTransactionRowModel>,
)

data class JournalFilterUi(
    val id: String,
    val label: String,
)

data class JournalListUiState(
    val query: String = "",
    val selectedFilterId: String = "all",
    val days: List<JournalDayUi> = emptyList(),
    val filters: List<JournalFilterUi> = journalFilters,
    val searchActive: Boolean = false,
    val isLoading: Boolean = false,
)

data class JournalLinkedTagUi(
    val title: String,
    val meta: String,
    val eventId: String? = null,
)

data class JournalDetailUiState(
    val id: String,
    val categoryId: String,
    val categoryLabel: String,
    val amountLabel: String,
    val dateTimeLabel: String,
    val note: String,
    val linkedTag: JournalLinkedTagUi? = null,
)

data class JournalQuickNoteUiState(
    val row: ProTransactionRowModel,
    val note: String,
)

val journalFilters = listOf(
    JournalFilterUi("all", "All"),
    JournalFilterUi("food", "Food"),
    JournalFilterUi("transport", "Transport"),
    JournalFilterUi("bills", "Bills"),
    JournalFilterUi("more", "More"),
)

private val journalToday = JournalDayUi(
    id = "2026-05-25",
    title = "Today · May 25",
    total = "$42",
    rows = listOf(
        ProTransactionRowModel("t1", "food", "Lunch with M.", "Food · 12:30 PM", "$12.40", detailDateTimeLabel = "Today · 12:30 PM"),
        ProTransactionRowModel("t2", "transport", "Transport", "Transport · 09:15 AM", "$3.50", detailDateTimeLabel = "Today · 09:15 AM"),
        ProTransactionRowModel("t3", "coffee", "Oat latte", "Coffee runs · 08:40 AM", "$5", detailDateTimeLabel = "Today · 08:40 AM"),
    ),
)

private val journalYesterday = JournalDayUi(
    id = "2026-05-24",
    title = "Yesterday · May 24",
    total = "$60",
    rows = listOf(
        ProTransactionRowModel(
            "y1", "entertainment", "Movie · Dune", "Entertainment · 08:10 PM", "$18",
            tag = "Bali Trip",
            detailDateTimeLabel = "Yesterday · 08:10 PM",
        ),
        ProTransactionRowModel("y2", "food", "Groceries", "Food · 05:30 PM", "$42", detailDateTimeLabel = "Yesterday · 05:30 PM"),
    ),
)

private val journalMonday = JournalDayUi(
    id = "2026-05-23",
    title = "Mon · May 23",
    total = "$8",
    rows = listOf(
        ProTransactionRowModel("m1", "transport", "Bus card", "Transport · 07:45 AM", "$8", detailDateTimeLabel = "Mon · May 23 · 07:45 AM"),
    ),
)

val previewJournalList = JournalListUiState(
    days = listOf(journalToday, journalYesterday, journalMonday),
)

val previewJournalSearchEmpty = JournalListUiState(
    query = "concert tickets",
    days = emptyList(),
    searchActive = true,
)

val previewJournalEmpty = JournalListUiState(
    days = emptyList(),
)

val previewJournalLoading = JournalListUiState(
    days = emptyList(),
    isLoading = true,
)

val previewJournalDetail = JournalDetailUiState(
    id = "t1",
    categoryId = "food",
    categoryLabel = "FOOD",
    amountLabel = "$12.40",
    dateTimeLabel = "Wed, May 25 · 12:30 PM",
    note = "Lunch with M. at the noodle place on 5th. Tip included.",
    linkedTag = JournalLinkedTagUi(
        title = "Bali Trip",
        meta = "Event · May 12 — May 26",
    ),
)

val previewJournalQuickNote = JournalQuickNoteUiState(
    row = ProTransactionRowModel("t1", "food", "Lunch with M.", "Food · 12:30 PM", "$12.40"),
    note = "Split the bill — M. covered the tip",
)
