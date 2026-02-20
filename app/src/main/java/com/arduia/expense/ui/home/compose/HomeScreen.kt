package com.arduia.expense.ui.home.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.home.IncomeOutcomeUiModel
import com.arduia.expense.ui.home.WeeklyGraphUiModel
import com.arduia.expense.ui.expenselogs.ExpenseUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    incomeOutcomeData: IncomeOutcomeUiModel?,
    graphData: WeeklyGraphUiModel?,
    recentData: List<ExpenseUiModel>,
    onMenuClick: () -> Unit,
    onMoreLogsClick: () -> Unit,
    onRecentItemClick: (ExpenseUiModel) -> Unit,
    onAddClick: ()-> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    onAddClick.invoke()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add Expense",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        // Use a background color to separate the white cards clearly
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp) // 8dp vertical spacing between cards
        ) {
            item {
                Spacer(modifier = Modifier.height(1.dp))
            }

            item {
                HomeInOutSummary(
                    data = incomeOutcomeData
                )
            }

            item {
                HomeExpenseGraph(
                    graphData = graphData
                )
            }

            if (recentData.isNotEmpty()) {
                item {
                    HomeRecentList(
                        recentData = recentData,
                        onItemClick = onRecentItemClick,
                        onMoreLogsClick = onMoreLogsClick
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(88.dp)) // Bottom padding for FAB space
            }
        }
    }
}
