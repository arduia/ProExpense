package com.arduia.expense.ui.component.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class CategoryStatisticUiModel(
    val name: String,
    val amount: String,
    val percentage: Float,
)

@Composable
fun CategoryStatisticRow(
    item: CategoryStatisticUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid1),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = item.amount,
                style = MaterialTheme.typography.bodyMedium,
                color = ProExpenseTheme.colors.negative,
            )
        }
        LinearProgressIndicator(
            progress = { item.percentage },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Preview
@Composable
private fun PreviewCategoryStatisticRow() {
    ProExpenseTheme {
        CategoryStatisticRow(
            item = CategoryStatisticUiModel(
                name = "Food & Drink",
                amount = "$320.00",
                percentage = 0.65f,
            ),
        )
    }
}
