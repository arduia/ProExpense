package com.arduia.expense.designsystem.component.form

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun <T> ToggleButtonGroup(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(text = label(option)) },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewToggleButtonGroup() {
    ProExpenseTheme {
        ToggleButtonGroup(
            options = listOf("Week", "Month", "Year"),
            selected = "Month",
            onSelect = {},
            label = { it },
        )
    }
}
