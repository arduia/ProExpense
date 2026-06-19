package com.arduia.expense.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.DayHeader
import com.arduia.expense.ui.design.FilterChip
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.JournalDayGroup
import com.arduia.expense.ui.preview.previewJournalFilters
import com.arduia.expense.ui.preview.previewJournalList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun JournalScreenContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    dayGroups: List<JournalDayGroup>,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val showEmpty = dayGroups.isEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
        contentPadding = PaddingValues(
            start = dimens.space18,
            end = dimens.space18,
            top = dimens.space24,
            bottom = dimens.space24 + dimens.navShellBottomInset,
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        item(key = "journal-header") {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space12)) {
                Text(
                    text = stringResource(R.string.journal),
                    style = typography.screenTitle,
                    color = colors.onSurface,
                )
                SearchField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = stringResource(R.string.journal_search_hint),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    filters.forEach { filter ->
                        FilterChip(
                            label = filter,
                            selected = filter == selectedFilter,
                            onClick = { onFilterSelected(filter) },
                        )
                    }
                }
            }
        }

        if (showEmpty) {
            item(key = "journal-empty") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = dimens.space32),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.journal_no_results),
                        style = typography.body,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
        } else {
            dayGroups.forEach { group ->
                item(key = "header-${group.dayTitle}") {
                    DayHeader(title = group.dayTitle, total = group.dayTotal)
                }
                items(
                    items = group.transactions,
                    key = { "${group.dayTitle}-${it.id}" },
                ) { transaction ->
                    TransactionRow(
                        categoryId = transaction.categoryId,
                        note = transaction.note,
                        meta = transaction.meta,
                        amount = transaction.amount,
                        tag = transaction.tag,
                        modifier = Modifier.proClickable(
                            onClick = { onTransactionClick(transaction.id) },
                            shape = ProExpenseTheme.shapes.searchField,
                        ),
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Journal — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListPreview() {
    ProExpenseTheme {
        JournalScreenContent(
            searchQuery = "",
            onSearchChange = {},
            filters = previewJournalFilters,
            selectedFilter = "All",
            onFilterSelected = {},
            dayGroups = previewJournalList,
            onTransactionClick = {},
        )
    }
}

@Preview(
    name = "Journal — no results",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalNoResultsPreview() {
    ProExpenseTheme {
        JournalScreenContent(
            searchQuery = "xyz",
            onSearchChange = {},
            filters = previewJournalFilters,
            selectedFilter = "All",
            onFilterSelected = {},
            dayGroups = emptyList(),
            onTransactionClick = {},
        )
    }
}
