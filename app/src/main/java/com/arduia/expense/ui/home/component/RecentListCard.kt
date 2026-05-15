package com.arduia.expense.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.component.card.DashboardCard
import com.arduia.expense.designsystem.component.state.NoDataPlaceholder
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews
import com.arduia.expense.ui.component.expense.ExpenseRow
import com.arduia.expense.ui.component.expense.ExpenseRowUiModel

@Composable
fun RecentListCard(
    expenses: List<ExpenseRowUiModel>,
    onSeeAllClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Recent",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            TextButton(onClick = onSeeAllClick) {
                Text(text = "See All")
            }
        }
        Column {
            if (expenses.isEmpty()) {
                NoDataPlaceholder(
                    message = "No expenses yet",
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                )
            } else {
                expenses.forEach { expense ->
                    ExpenseRow(item = expense, onClick = onExpenseClick)
                }
            }
        }
    }
}

private val previewExpenses = listOf(
    ExpenseRowUiModel(id = 1L, categoryInitial = "F", name = "Grocery", date = "Today", amount = "- $45.00", isExpense = true),
    ExpenseRowUiModel(id = 2L, categoryInitial = "T", name = "Bus Fare", date = "Yesterday", amount = "- $2.50", isExpense = true),
    ExpenseRowUiModel(id = 3L, categoryInitial = "S", name = "Salary", date = "May 1", amount = "+ $3,200.00", isExpense = false),
)

@ThemePreviews
@Composable
private fun PreviewRecentListCardFilled() {
    ProExpenseTheme {
        RecentListCard(expenses = previewExpenses, onSeeAllClick = {}, onExpenseClick = {})
    }
}

@Preview(name = "Empty state")
@Composable
private fun PreviewRecentListCardEmpty() {
    ProExpenseTheme {
        RecentListCard(expenses = emptyList(), onSeeAllClick = {}, onExpenseClick = {})
    }
}
