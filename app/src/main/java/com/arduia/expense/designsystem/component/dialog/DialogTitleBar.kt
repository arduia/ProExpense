package com.arduia.expense.designsystem.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun DialogTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        actions()
    }
}

@Preview
@Composable
private fun PreviewDialogTitleBar() {
    ProExpenseTheme {
        DialogTitleBar(title = "Expense Detail")
    }
}
