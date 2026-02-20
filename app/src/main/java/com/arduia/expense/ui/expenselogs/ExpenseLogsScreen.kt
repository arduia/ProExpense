package com.arduia.expense.ui.expenselogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.arduia.expense.R
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsEvent
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseLogsScreen(
    state: ExpenseLogsState,
    pagingItems: LazyPagingItems<ExpenseLogUiModel>,
    selectedIds: Set<Int>,
    onEvent: (ExpenseLogsEvent) -> Unit,
    onItemClick: (ExpenseLogUiModel.Log) -> Unit,
    onItemDeleteClick: (ExpenseLogUiModel.Log) -> Unit,
    onItemSelectionToggle: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        if (state.mode == ExpenseMode.SELECTION) {
                            Text(text = "${state.selectedCount} Selected")
                        } else {
                            Text(text = stringResource(id = R.string.expense_logs))
                            if (state.filterDateRangeText.isNotEmpty()) {
                                Text(
                                    text = state.filterDateRangeText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (state.mode == ExpenseMode.SELECTION) {
                        IconButton(onClick = { onEvent(ExpenseLogsEvent.SelectionCloseClicked) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Close Selection"
                            )
                        }
                    } else {
                        IconButton(onClick = { onEvent(ExpenseLogsEvent.NavDrawerClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                },
                actions = {
                    if (state.mode == ExpenseMode.SELECTION) {
                        IconButton(onClick = { onEvent(ExpenseLogsEvent.DeleteButtonClicked) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onEvent(ExpenseLogsEvent.FilterButtonClicked) },
                            enabled = state.isFilterEnabled
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Filter"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.isEmptyLogs) {
                EmptyLogsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = pagingItems.itemCount) { index ->
                        val item = pagingItems[index] ?: return@items
                        when (item) {
                            is ExpenseLogUiModel.Header -> {
                                ExpenseLogHeaderItem(date = item.date)
                            }
                            is ExpenseLogUiModel.Log -> {
                                ExpenseLogItem(
                                    logItem = item,
                                    isSelected = selectedIds.contains(item.expenseLog.id),
                                    onClick = { onItemClick(item) },
                                    onDeleteClick = { onItemDeleteClick(item) },
                                    onSelectionToggle = { onItemSelectionToggle(item.expenseLog.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLogsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = R.string.no_data),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}
