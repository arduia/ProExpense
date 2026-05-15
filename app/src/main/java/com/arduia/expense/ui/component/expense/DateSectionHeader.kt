package com.arduia.expense.ui.component.expense

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun DateSectionHeader(
    date: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(
            horizontal = ProExpenseTheme.spacing.grid3,
            vertical = ProExpenseTheme.spacing.grid2,
        ),
        text = date,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Preview
@Composable
private fun PreviewDateSectionHeader() {
    ProExpenseTheme {
        DateSectionHeader(date = "Today")
    }
}
