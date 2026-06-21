package com.arduia.expense.feature.importexport.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ExportSettingsFlow(
    onBack: () -> Unit,
    onExport: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    MoreExportScreen(
        files = previewMoreExportFiles,
        onExport = onExport,
        onBack = onBack,
        modifier = modifier,
    )
}

@Preview(
    name = "Export settings flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ExportSettingsFlowPreview() {
    ProExpenseTheme {
        ExportSettingsFlow(onBack = {}, onExport = {})
    }
}
