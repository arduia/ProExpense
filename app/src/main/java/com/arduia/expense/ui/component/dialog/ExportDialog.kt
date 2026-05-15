package com.arduia.expense.ui.component.dialog

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
import com.arduia.expense.designsystem.component.form.ToggleButtonGroup
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class ExportUiState(
    val selectedRange: String,
    val availableRanges: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
    state: ExportUiState,
    onRangeSelect: (String) -> Unit,
    onExport: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(onDismissRequest = onDismiss, modifier = modifier) {
        Surface(shape = MaterialTheme.shapes.large) {
            Column {
                DialogTitleBar(title = "Export to Excel")
                HorizontalDivider(color = ProExpenseTheme.colors.divider)
                Column(modifier = Modifier.padding(ProExpenseTheme.spacing.grid3)) {
                    Text(
                        text = "Select Range",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid2))
                    ToggleButtonGroup(
                        options = state.availableRanges,
                        selected = state.selectedRange,
                        onSelect = onRangeSelect,
                        label = { it },
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ProExpenseTheme.spacing.grid2,
                            vertical = ProExpenseTheme.spacing.grid1,
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text(text = "Cancel") }
                    TextButton(onClick = onExport) { Text(text = "Export") }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewExportDialog() {
    ProExpenseTheme {
        ExportDialog(
            state = ExportUiState(
                selectedRange = "Month",
                availableRanges = listOf("Week", "Month", "Year", "All"),
            ),
            onRangeSelect = {},
            onExport = {},
            onDismiss = {},
        )
    }
}
