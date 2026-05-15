package com.arduia.expense.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.form.ToggleButtonGroup
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.category.CategoryStatisticRow
import com.arduia.expense.ui.home.component.SpendGraphCard

@Composable
fun StatisticsScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StatisticsContent(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onRangeSelect = viewModel::onRangeSelect,
        modifier = modifier,
    )
}

@Composable
private fun StatisticsContent(
    uiState: StatisticsUiState,
    onOpenDrawer: () -> Unit,
    onRangeSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Statistics",
                navIcon = TopBarNavIcon.Drawer,
                onNavIconClick = onOpenDrawer,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + ProExpenseTheme.spacing.grid3,
                bottom = innerPadding.calculateBottomPadding() + ProExpenseTheme.spacing.grid3,
                start = ProExpenseTheme.spacing.grid3,
                end = ProExpenseTheme.spacing.grid3,
            ),
            verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        ) {
            item {
                ToggleButtonGroup(
                    options = uiState.availableRanges,
                    selected = uiState.selectedRange,
                    onSelect = onRangeSelect,
                    label = { it },
                )
            }
            item {
                SpendGraphCard(weeklyData = uiState.weeklyGraphData)
            }
            items(uiState.categoryBreakdown) { category ->
                CategoryStatisticRow(item = category)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewStatisticsContent() {
    ProExpenseTheme {
        StatisticsContent(
            uiState = StatisticsUiState(),
            onOpenDrawer = {},
            onRangeSelect = {},
        )
    }
}
