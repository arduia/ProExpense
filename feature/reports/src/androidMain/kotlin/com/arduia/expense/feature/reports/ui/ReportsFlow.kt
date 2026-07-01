package com.arduia.expense.feature.reports.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Reports UI flow. [periods] cycles via period chevrons; [empty] shows new-user state.
 */
@Composable
fun ReportsFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    periods: List<ReportsUiState> = listOf(previewReports, previewReportsUncategorized),
    initialPage: Int = 0,
    empty: Boolean = false,
    onLogFirstExpense: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ReportsScreen(
                state = if (empty || periods.isEmpty()) previewReportsEmpty else periods[page % periods.size],
                onBack = onBack,
                onPrevPeriod = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1 + periods.size) % periods.size.coerceAtLeast(1))
                    }
                },
                onNextPeriod = {
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1) % periods.size.coerceAtLeast(1))
                    }
                },
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
