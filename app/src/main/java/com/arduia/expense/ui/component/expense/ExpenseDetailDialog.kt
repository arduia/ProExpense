package com.arduia.expense.ui.component.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.component.dialog.DialogTitleBar
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class ExpenseDetailUiModel(
    val id: Long,
    val name: String,
    val amount: String,
    val category: String,
    val date: String,
    val note: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailDialog(
    expense: ExpenseDetailUiModel,
    onDismiss: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(onDismissRequest = onDismiss, modifier = modifier) {
        Surface(shape = MaterialTheme.shapes.large) {
            Column {
                DialogTitleBar(title = expense.name)
                HorizontalDivider(color = ProExpenseTheme.colors.divider)
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
                ) {
                    DetailRow(label = "Amount", value = expense.amount)
                    DetailRow(label = "Category", value = expense.category)
                    DetailRow(label = "Date", value = expense.date)
                    if (expense.note.isNotBlank()) {
                        DetailRow(label = "Note", value = expense.note)
                    }
                }
                Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid2))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ProExpenseTheme.spacing.grid2),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onDelete(expense.id) }) {
                        Text(text = "Delete", color = ProExpenseTheme.colors.negative)
                    }
                    TextButton(onClick = { onEdit(expense.id) }) {
                        Text(text = "Edit")
                    }
                }
                Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid2))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun PreviewExpenseDetailDialog() {
    ProExpenseTheme {
        ExpenseDetailDialog(
            expense = ExpenseDetailUiModel(
                id = 1L,
                name = "Grocery",
                amount = "- $45.00",
                category = "Food & Drink",
                date = "May 15, 2026",
                note = "Weekly shopping",
            ),
            onDismiss = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
