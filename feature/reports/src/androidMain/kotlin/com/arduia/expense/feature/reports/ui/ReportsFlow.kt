package com.arduia.expense.feature.reports.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Host for Reports. The period chevrons cycle the supplied [periods] (one per month with spend);
 * [empty] launches the new-user "no data yet" state. Defaults to the demo months so previews and
 * screenshot tests render without a wired ViewModel.
 */
@Composable
fun ReportsFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    empty: Boolean = false,
    onLogFirstExpense: () -> Unit = {},
    periods: List<ReportsUiState> = remember { listOf(previewReports, previewReportsUncategorized) },
) {
    val colors = ProExpenseTheme.colors
    var index by remember(periods) { mutableIntStateOf(0) }
    val showEmpty = empty || periods.isEmpty()
    val safeIndex = index.coerceIn(0, (periods.size - 1).coerceAtLeast(0))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        ReportsScreen(
            state = if (showEmpty) previewReportsEmpty else periods[safeIndex],
            onBack = onBack,
            onPrevPeriod = { if (periods.isNotEmpty()) index = (safeIndex - 1 + periods.size) % periods.size },
            onNextPeriod = { if (periods.isNotEmpty()) index = (safeIndex + 1) % periods.size },
            onLogFirstExpense = onLogFirstExpense,
        )
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
