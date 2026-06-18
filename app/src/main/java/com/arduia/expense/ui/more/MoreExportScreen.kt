package com.arduia.expense.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.design.ProExpenseButton
import com.arduia.expense.ui.design.ProExpenseButtonSize
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

data class ExportFile(
    val fileName: String,
    val description: String,
)

val DefaultExportFiles: List<ExportFile> = listOf(
    ExportFile("expenses.csv", "All logged expenses + @ tags"),
    ExportFile("events.csv", "Event budgets & status"),
    ExportFile("debts.csv", "Lent / owed records"),
    ExportFile("shared_costs.csv", "Saved bill splits"),
)

/**
 * Export all local data as a ZIP of CSVs. Reinforces the offline / local-only promise — nothing is
 * uploaded. See design/screens/06-more/05-more-export.
 */
@Composable
fun MoreExportScreen(
    onBack: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
    files: List<ExportFile> = DefaultExportFiles,
) {
    val colors = ProExpenseTheme.colors

    Column(modifier = modifier.fillMaxSize()) {
        MoreNavBar(title = "Data Export", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MoreHorizontalPadding),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            IconTile(tint = colors.primaryTint, size = 64.dp) {
                FileGlyph(color = colors.primaryDeep, modifier = it)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Export your data",
                style = ProExpenseTheme.typography.sectionHead.copy(fontSize = 23.sp),
                color = colors.ink,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "One zip with a CSV per feature. Everything stays on your device — " +
                    "nothing is uploaded.",
                style = ProExpenseTheme.typography.body,
                color = colors.muted,
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsCard(
                rows = files.map { file ->
                    { ExportFileRow(file = file) }
                },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        ProExpenseButton(
            text = "Export as ZIP",
            onClick = onExport,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoreHorizontalPadding)
                .padding(top = 12.dp, bottom = 38.dp),
            size = ProExpenseButtonSize.Large,
        )
    }
}

@Composable
private fun ExportFileRow(
    file: ExportFile,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = ProExpenseTheme.shapes.buttonSmall,
            color = colors.paper,
        ) {
            Box(contentAlignment = Alignment.Center) {
                FileGlyph(color = colors.ink3, modifier = Modifier.size(16.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.fileName,
                style = ProExpenseTheme.typography.monoCaption.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = colors.ink,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = file.description,
                style = ProExpenseTheme.typography.caption,
                color = colors.muted,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun MoreExportScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            MoreExportScreen(
                onBack = {},
                onExport = {},
            )
        }
    }
}
