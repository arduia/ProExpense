package com.arduia.expense.feature.history.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.DayHeader
import com.arduia.expense.ui.design.FilterChip
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.feature.history.ui.preview.JournalListUiState
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.feature.history.ui.preview.previewJournalSearchEmpty
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JournalListScreen(
    state: JournalListUiState,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onRowClick: (ProTransactionRowModel) -> Unit,
    onRowLongPress: (ProTransactionRowModel) -> Unit,
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    dateRangeLabel: String? = null,
    onDateRangeClick: () -> Unit = {},
    onClearDateRange: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space14),
                verticalArrangement = Arrangement.spacedBy(dimens.space16),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    if (!state.searchActive) {
                        Text(
                            text = stringResource(R.string.journal_eyebrow),
                            style = typography.eyebrow,
                            color = colors.muted,
                        )
                    }
                    Text(
                        text = stringResource(R.string.journal_title),
                        style = typography.profileScreenTitle,
                        color = colors.onSurface,
                    )
                }

                SearchField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.journal_search_placeholder),
                    active = state.searchActive,
                )

                // The date-range chip stays visible during search — an active range still
                // constrains results, and hiding it would make "no matches" misleading. Category
                // chips still hide, since they'd otherwise crowd out the query results list.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    DateRangeChip(
                        label = dateRangeLabel,
                        onClick = onDateRangeClick,
                        onClear = onClearDateRange,
                    )
                    if (!state.searchActive) {
                        state.filters.forEach { filter ->
                            FilterChip(
                                label = filter.label,
                                selected = filter.id == state.selectedFilterId,
                                onClick = { onFilterSelected(filter.id) },
                            )
                        }
                    }
                }
            }

            if (state.searchActive && state.days.isEmpty()) {
                JournalNoResults(
                    query = state.query,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding),
                    contentPadding = PaddingValues(
                        top = dimens.space12,
                        bottom = dimens.navShellBottomInset,
                    ),
                ) {
                    state.days.forEach { day ->
                        item(key = "header-${day.id}") {
                            DayHeader(title = day.title, total = day.total)
                        }
                        itemsIndexed(
                            items = day.rows,
                            key = { _, row -> row.id },
                        ) { index, row ->
                            Box(
                                modifier = Modifier.combinedClickable(
                                    onClick = { onRowClick(row) },
                                    onLongClick = { onRowLongPress(row) },
                                ),
                            ) {
                                TransactionRow(
                                    categoryId = row.categoryId,
                                    note = row.note,
                                    meta = row.meta,
                                    amount = row.amount,
                                    tag = row.tag,
                                    showDivider = index < day.rows.lastIndex,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!state.searchActive) {
            HomeBottomNav(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onAddClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun DateRangeChip(
    label: String?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.chip
    val contentDescription = stringResource(R.string.journal_date_range_cd)

    if (label == null) {
        Box(
            modifier = modifier
                .size(dimens.space44)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, colors.lineStrong), CircleShape)
                .proIconClickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.Calendar,
                contentDescription = contentDescription,
                tint = colors.onSurfaceVariant,
                size = dimens.iconInline,
            )
        }
    } else {
        Row(
            modifier = modifier
                .clip(shape)
                .background(colors.onSurface)
                .border(BorderStroke(1.dp, colors.onSurface), shape)
                .proClickable(onClick = onClick, shape = shape)
                .padding(start = dimens.space12, end = dimens.space8, top = dimens.space6, bottom = dimens.space6),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            Text(
                text = label,
                style = typography.chipLabelSelected,
                color = colors.paper,
            )
            ProIcon(
                glyph = ProIconGlyph.Close,
                contentDescription = stringResource(R.string.journal_date_range_clear),
                tint = colors.paper,
                size = dimens.iconClear,
                modifier = Modifier.proIconClickable(onClick = onClear),
            )
        }
    }
}

@Composable
private fun JournalNoResults(
    query: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val titleStyle = typography.bodySemiBold.copy(
        fontSize = typography.sectionHead.fontSize,
        lineHeight = typography.sectionHead.lineHeight,
    )

    Column(
        modifier = modifier.padding(horizontal = dimens.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Search,
            contentDescription = null,
            tint = colors.muted2,
            size = dimens.space32,
        )
        Text(
            text = stringResource(R.string.journal_no_matches_title),
            style = titleStyle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space18),
        )
        Text(
            text = stringResource(R.string.journal_no_matches_body, query),
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space8),
        )
    }
}

@Preview(
    name = "Journal — date grouped",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListPreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalList,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
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
private fun JournalSearchEmptyPreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalSearchEmpty,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}

@Preview(
    name = "Journal — date range active",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListDateRangeActivePreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalList,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
            dateRangeLabel = "May 1 – May 15",
        )
    }
}
