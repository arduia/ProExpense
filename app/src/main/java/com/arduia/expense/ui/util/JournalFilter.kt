package com.arduia.expense.ui.util

import com.arduia.expense.ui.preview.JournalDayGroup
import com.arduia.expense.ui.preview.JournalTransactionItem

fun filterJournalGroups(
    groups: List<JournalDayGroup>,
    searchQuery: String,
    selectedFilter: String,
): List<JournalDayGroup> {
    val query = searchQuery.trim().lowercase()
    return groups.mapNotNull { group ->
        val transactions = group.transactions.filter { transaction ->
            transaction.matchesFilter(selectedFilter) && transaction.matchesQuery(query)
        }
        if (transactions.isEmpty()) {
            null
        } else {
            group.copy(transactions = transactions)
        }
    }
}

private fun JournalTransactionItem.matchesQuery(query: String): Boolean {
    if (query.isEmpty()) return true
    return note.lowercase().contains(query) ||
        meta.lowercase().contains(query) ||
        categoryId.lowercase().contains(query) ||
        (tag?.lowercase()?.contains(query) == true)
}

private fun JournalTransactionItem.matchesFilter(filter: String): Boolean {
    if (filter == "All") return true
    if (filter == "More") {
        return categoryId !in setOf("food", "transport", "bills")
    }
    return meta.lowercase().contains(filter.lowercase()) ||
        categoryId.equals(filter, ignoreCase = true)
}
