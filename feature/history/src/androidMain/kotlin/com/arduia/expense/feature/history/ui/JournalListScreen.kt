package com.arduia.expense.feature.history.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.semantics.Role
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.DayHeader
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.FilterChip
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proCircularRippleClickable
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.history.ui.preview.JournalListUiState
import com.arduia.expense.feature.history.ui.preview.previewJournalEmpty
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.feature.history.ui.preview.previewJournalLoading
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
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= layoutInfo.totalItemsCount - 5
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

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
                            color = colors.onSurfaceMuted,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.journal_title),
                            style = typography.profileScreenTitle,
                            color = colors.onSurface,
                        )
                        // No proIconClickable here — its 48dp minimum-size box reads as
                        // oversized/misaligned next to a compact icon in a tight header row (same
                        // class of defect as the date-range chip's clear icon). iconInline (not
                        // iconNav) matches the icons.md spec's "list 16-18dp" category — this is
                        // an inline header affordance, not a full-size bottom-nav icon.
                        Box(
                            // Clip + ripple sit on this outer box, not on the icon itself — the
                            // calendar glyph's frame corners sit almost exactly at its own bounding
                            // box edge (bottom corners measure ~12.04 units from center in a 24-unit
                            // viewBox whose inscribed-circle radius is 12), so clipping the icon
                            // directly to a same-size CircleShape shaves the corners off. Padding
                            // before the clip gives the icon room inside the circle instead.
                            modifier = Modifier
                                .clip(CircleShape)
                                .proCircularRippleClickable(onClick = onDateRangeClick, role = Role.Button)
                                .padding(dimens.space4),
                            contentAlignment = Alignment.Center,
                        ) {
                            ProIcon(
                                glyph = ProIconGlyph.Calendar,
                                contentDescription = stringResource(R.string.journal_date_range_cd),
                                tint = colors.onSurfaceVariant,
                                size = dimens.iconInline,
                            )
                        }
                    }
                }

                SearchField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.journal_search_placeholder),
                    active = state.searchActive,
                )
            }

            // Outside the screen's padded Column so the scrollable viewport spans the full
            // width — chips scroll flush to both screen edges instead of stopping at an
            // invisible wall inset by screenPadding on either side. Leading/trailing spacers
            // reproduce that same inset only for the resting (unscrolled) position.
            // The date-range chip stays visible during search — an active range still
            // constrains results, and hiding it would make "no matches" misleading. Category
            // chips still hide, since they'd otherwise crowd out the query results list.
            if (dateRangeLabel != null || !state.searchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = dimens.space16),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space6),
                ) {
                    // spacedBy also gaps the leading/trailing Spacer from its neighboring chip,
                    // so each Spacer is shortened by that gap to land the resting inset exactly
                    // on screenPadding — matching the title/search field above.
                    Spacer(Modifier.width(dimens.screenPadding - dimens.space6))
                    if (dateRangeLabel != null) {
                        DateRangeChip(
                            label = dateRangeLabel,
                            onClick = onDateRangeClick,
                            onClear = onClearDateRange,
                        )
                    }
                    if (!state.searchActive) {
                        state.filters.forEach { filter ->
                            FilterChip(
                                label = filter.label,
                                selected = filter.id == state.selectedFilterId,
                                onClick = { onFilterSelected(filter.id) },
                            )
                        }
                    }
                    Spacer(Modifier.width(dimens.screenPadding - dimens.space6))
                }
            }

            val hasActiveFilter = dateRangeLabel != null || state.selectedFilterId != "all"
            if (state.isLoading) {
                // Records load asynchronously after first composition — without this, "no data
                // yet" and "genuinely no records" render identically and the empty-state
                // illustration flashes on every visit for a user who actually has records.
                Box(modifier = Modifier.weight(1f).fillMaxWidth())
            } else if (state.searchActive && state.days.isEmpty()) {
                JournalNoResults(
                    query = state.query,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else if (!state.searchActive && state.days.isEmpty() && hasActiveFilter) {
                JournalFilteredEmpty(
                    onClearFilter = {
                        onClearDateRange()
                        onFilterSelected("all")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else if (!state.searchActive && state.days.isEmpty()) {
                EmptyStateContent(
                    title = stringResource(R.string.journal_empty_title),
                    subtitle = stringResource(R.string.journal_empty_body),
                    actionLabel = stringResource(R.string.journal_empty_action),
                    onActionClick = onAddClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                LazyColumn(
                    state = listState,
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
                    if (isLoadingMore) {
                        item(key = "journal-loading-more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimens.space16),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .height(dimens.iconInline)
                                        .width(dimens.iconInline),
                                    color = colors.onSurfaceMuted,
                                    strokeWidth = 2.dp,
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
    label: String,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.chip

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
        // No proIconClickable here — its 48dp minimum-size box would double the chip's height
        // relative to the FilterChips beside it. The X is a secondary micro-target inside an
        // already-tappable chip, so an unbounded ripple without size inflation is enough.
        ProIcon(
            glyph = ProIconGlyph.Close,
            contentDescription = stringResource(R.string.journal_date_range_clear),
            tint = colors.paper,
            size = dimens.iconClear,
            modifier = Modifier
                .clip(CircleShape)
                .proCircularRippleClickable(onClick = onClear, role = Role.Button),
        )
    }
}

@Composable
private fun JournalFilteredEmpty(
    onClearFilter: () -> Unit,
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
            glyph = ProIconGlyph.Calendar,
            contentDescription = null,
            tint = colors.muted2,
            size = dimens.space32,
        )
        Text(
            text = stringResource(R.string.journal_filtered_empty_title),
            style = titleStyle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space18),
        )
        Text(
            text = stringResource(R.string.journal_filtered_empty_body),
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space8),
        )
        ProButton(
            text = stringResource(R.string.journal_filtered_empty_action),
            onClick = onClearFilter,
            variant = ProButtonVariant.Secondary,
            size = ProButtonSize.Md,
            modifier = Modifier.padding(top = dimens.space20),
        )
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

@Preview(
    name = "Journal — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListEmptyPreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalEmpty,
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
    name = "Journal — loading",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListLoadingPreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalLoading,
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
    name = "Journal — filtered empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListFilteredEmptyPreview() {
    ProExpenseTheme {
        JournalListScreen(
            state = previewJournalEmpty,
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

@Preview(
    name = "Journal — loading more",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalListLoadingMorePreview() {
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
            isLoadingMore = true,
        )
    }
}
