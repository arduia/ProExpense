package com.arduia.expense.designsystem.component.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@Composable
fun NoDataPlaceholder(
    message: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(ProExpenseTheme.spacing.grid4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewNoDataPlaceholderTextOnly() {
    ProExpenseTheme {
        NoDataPlaceholder(message = "No expenses yet.\nAdd your first expense!")
    }
}

@Preview(name = "With icon")
@Composable
private fun PreviewNoDataPlaceholderWithIcon() {
    ProExpenseTheme {
        NoDataPlaceholder(
            message = "No backup files yet",
            icon = painterResource(R.drawable.ic_backup),
        )
    }
}
