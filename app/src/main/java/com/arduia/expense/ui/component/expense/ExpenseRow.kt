package com.arduia.expense.ui.component.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.component.badge.CircularBadge
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class ExpenseRowUiModel(
    val id: Long,
    val categoryInitial: String,
    val name: String,
    val date: String,
    val amount: String,
    val isExpense: Boolean,
)

@Composable
fun ExpenseRow(
    item: ExpenseRowUiModel,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularBadge(label = item.categoryInitial)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = item.amount,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isExpense) ProExpenseTheme.colors.negative else ProExpenseTheme.colors.positive,
        )
    }
}

@Preview
@Composable
private fun PreviewExpenseRow() {
    ProExpenseTheme {
        ExpenseRow(
            item = ExpenseRowUiModel(
                id = 1L,
                categoryInitial = "F",
                name = "Food & Drink",
                date = "Today, 10:30 AM",
                amount = "- $12.50",
                isExpense = true,
            ),
        )
    }
}
