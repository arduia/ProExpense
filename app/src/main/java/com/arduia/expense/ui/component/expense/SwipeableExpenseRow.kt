package com.arduia.expense.ui.component.expense

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableExpenseRow(
    item: ExpenseRowUiModel,
    onDelete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onDelete(item.id); true }
                SwipeToDismissBoxValue.StartToEnd -> { onEdit(item.id); false }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> ProExpenseTheme.colors.negative
                    SwipeToDismissBoxValue.StartToEnd -> ProExpenseTheme.colors.warning
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "swipe-bg-color",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = ProExpenseTheme.spacing.grid3),
            ) {
                when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> Icon(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError,
                    )
                    SwipeToDismissBoxValue.StartToEnd -> Icon(
                        modifier = Modifier.align(Alignment.CenterStart),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    else -> Unit
                }
            }
        },
    ) {
        ExpenseRow(item = item, onClick = {})
    }
}

@ThemePreviews
@Composable
private fun PreviewSwipeableExpenseRow() {
    ProExpenseTheme {
        SwipeableExpenseRow(
            item = ExpenseRowUiModel(
                id = 1L, categoryInitial = "F", name = "Food & Drink",
                date = "Today, 10:30 AM", amount = "- $12.50", isExpense = true,
            ),
            onDelete = {}, onEdit = {},
        )
    }
}

@Preview(name = "Income item")
@Composable
private fun PreviewSwipeableIncomeRow() {
    ProExpenseTheme {
        SwipeableExpenseRow(
            item = ExpenseRowUiModel(
                id = 2L, categoryInitial = "S", name = "Salary",
                date = "May 1, 2026", amount = "+ $3,200.00", isExpense = false,
            ),
            onDelete = {}, onEdit = {},
        )
    }
}
