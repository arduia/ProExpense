package com.arduia.expense.feature.reports.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.arduia.expense.feature.reports.R
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.feature.reports.ui.preview.previewReportsWeekly
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Reports UI flow. [periods] cycles via period chevrons or a swipe on the body below the pill.
 * The top bar and month pill are fixed chrome, hoisted above the pager — only the content
 * underneath (total, donut, categories) pages between periods, so switching months never reads
 * like navigating to a new screen.
 */
@Composable
fun ReportsFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    periods: List<ReportsUiState> = listOf(previewReports, previewReportsUncategorized),
    initialPage: Int = 0,
    empty: Boolean = false,
    onLogFirstExpense: () -> Unit = {},
    granularityIndex: Int = 0,
    onGranularityChange: (Int) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (periods.size - 1).coerceAtLeast(0)),
        pageCount = { periods.size.coerceAtLeast(1) },
    )
    val scope = rememberCoroutineScope()

    // `periods` loads asynchronously after first composition, so `initialPage` above is frozen at
    // its stale (usually 0) value from before data arrived — re-sync once the real period list
    // (and its computed target page) shows up, so the auto-fallback to the latest month with data
    // (US-REP-3 Scenario 2) actually takes effect instead of leaving the pager stuck on page 0.
    LaunchedEffect(periods, initialPage) {
        val targetPage = initialPage.coerceIn(0, (periods.size - 1).coerceAtLeast(0))
        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    val globalEmpty = empty || periods.isEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.reports_title),
                onBack = onBack,
                backLabel = stringResource(R.string.reports_back),
            )
        }

        if (globalEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.screenPadding),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateContent(
                    title = stringResource(R.string.reports_empty_title),
                    subtitle = stringResource(R.string.reports_empty_subtitle),
                    actionLabel = stringResource(R.string.reports_empty_action),
                    onActionClick = onLogFirstExpense,
                )
            }
            return@Column
        }

        SegmentedToggle(
            options = listOf(
                stringResource(R.string.reports_granularity_month),
                stringResource(R.string.reports_granularity_week),
            ),
            selectedIndex = granularityIndex,
            onSelected = onGranularityChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8),
        )

        val currentPeriod = periods[pagerState.currentPage % periods.size]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8),
            contentAlignment = Alignment.Center,
        ) {
            ReportsPeriodPill(
                label = currentPeriod.periodLabel,
                onPrev = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1 + periods.size) % periods.size.coerceAtLeast(1))
                    }
                },
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1) % periods.size.coerceAtLeast(1))
                    }
                },
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ReportsPeriodContent(
                state = periods[page % periods.size],
                onLogFirstExpense = onLogFirstExpense,
            )
        }
    }
}

@Preview(
    name = "Reports flow — data",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsFlowPreview() {
    ProExpenseTheme {
        ReportsFlow(onBack = {})
    }
}

@Preview(
    name = "Reports flow — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsFlowEmptyPreview() {
    ProExpenseTheme {
        ReportsFlow(onBack = {}, empty = true)
    }
}

@Preview(
    name = "Reports flow — weekly",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsFlowWeeklyPreview() {
    ProExpenseTheme {
        ReportsFlow(
            onBack = {},
            periods = listOf(previewReportsWeekly),
            granularityIndex = 1,
        )
    }
}
