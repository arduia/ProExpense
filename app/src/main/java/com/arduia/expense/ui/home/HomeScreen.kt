package com.arduia.expense.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.home.component.RecentListCard
import com.arduia.expense.ui.home.component.SpendGraphCard
import com.arduia.expense.ui.home.component.TotalsCard

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToEntry: () -> Unit,
    onNavigateToLog: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onAddExpenseClick = onNavigateToEntry,
        onSeeAllClick = onNavigateToLog,
        onExpenseClick = onExpenseClick,
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onOpenDrawer: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Home",
                navIcon = TopBarNavIcon.Drawer,
                onNavIconClick = onOpenDrawer,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add expense")
            }
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
                TotalsCard(
                    totalExpense = uiState.totalExpense,
                    totalIncome = uiState.totalIncome,
                    totalBalance = uiState.totalBalance,
                )
            }
            item {
                SpendGraphCard(weeklyData = uiState.weeklyGraphData)
            }
            item {
                RecentListCard(
                    expenses = uiState.recentExpenses,
                    onSeeAllClick = onSeeAllClick,
                    onExpenseClick = onExpenseClick,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeContent() {
    ProExpenseTheme {
        HomeContent(
            uiState = HomeUiState(
                totalExpense = "$320.00",
                totalIncome = "$1,200.00",
                totalBalance = "$880.00",
                weeklyGraphData = listOf(100f, 250f, 80f, 320f, 150f, 200f, 90f),
            ),
            onOpenDrawer = {},
            onAddExpenseClick = {},
            onSeeAllClick = {},
            onExpenseClick = {},
        )
    }
}
