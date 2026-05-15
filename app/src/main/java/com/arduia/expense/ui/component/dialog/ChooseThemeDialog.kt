package com.arduia.expense.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

enum class AppTheme { System, Light, Dark }

@Composable
fun ChooseThemeDialog(
    currentTheme: AppTheme,
    onThemeSelect: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose Theme") },
        text = {
            Column {
                AppTheme.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = ProExpenseTheme.spacing.grid1),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = { onThemeSelect(theme) },
                        )
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = "Close") }
        },
    )
}

@Preview
@Composable
private fun PreviewChooseThemeDialog() {
    ProExpenseTheme {
        ChooseThemeDialog(
            currentTheme = AppTheme.System,
            onThemeSelect = {},
            onDismiss = {},
        )
    }
}
