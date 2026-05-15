package com.arduia.expense.ui.log

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.state.NoDataPlaceholder
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.expense.DateSectionHeader
import com.arduia.expense.ui.component.expense.SwipeableExpenseRow

@Composable
fun ExpenseLogScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseLogViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items by viewModel.expenseItems.collectAsStateWithLifecycle()
    ExpenseLogContent(
        uiState = uiState,
        items = items,
        onNavigateBack = onNavigateBack,
        onDelete = viewModel::onDelete,
        onEdit = onNavigateToEdit,
        modifier = modifier,
    )
}

@Composable
private fun ExpenseLogContent(
    uiState: ExpenseLogUiState,
    items: List<ExpenseLogItem>,
    onNavigateBack: () -> Unit,
    onDelete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Expense Log",
                navIcon = TopBarNavIcon.Back,
                onNavIconClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        if (items.isEmpty() && !uiState.isLoading) {
            NoDataPlaceholder(
                message = "No expenses found",
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(items, key = { item ->
                    when (item) {
                        is ExpenseLogItem.Header -> "header_${item.date}"
                        is ExpenseLogItem.Row -> "row_${item.expense.id}"
                    }
                }) { item ->
                    when (item) {
                        is ExpenseLogItem.Header -> DateSectionHeader(date = item.date)
                        is ExpenseLogItem.Row -> SwipeableExpenseRow(
                            item = item.expense,
                            onDelete = onDelete,
                            onEdit = onEdit,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewExpenseLogContent() {
    ProExpenseTheme {
        ExpenseLogContent(
            uiState = ExpenseLogUiState(),
            items = emptyList(),
            onNavigateBack = {},
            onDelete = {},
            onEdit = {},
        )
    }
}
