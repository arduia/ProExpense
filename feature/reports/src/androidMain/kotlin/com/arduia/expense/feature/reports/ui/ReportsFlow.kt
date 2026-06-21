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
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Mockup host for Reports. With data, the period chevrons cycle the demo months so every
 * state is reachable for testing; [empty] launches the new-user "no data yet" state.
 */
@Composable
fun ReportsFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    empty: Boolean = false,
    onLogFirstExpense: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val periods = remember { listOf(previewReports, previewReportsUncategorized) }
    var index by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        ReportsScreen(
            state = if (empty) previewReportsEmpty else periods[index],
            onBack = onBack,
            onPrevPeriod = { index = (index - 1 + periods.size) % periods.size },
            onNextPeriod = { index = (index + 1) % periods.size },
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
