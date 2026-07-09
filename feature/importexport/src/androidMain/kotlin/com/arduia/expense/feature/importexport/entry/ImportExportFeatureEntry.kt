package com.arduia.expense.feature.importexport.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.importexport.ui.ClearDataFlow
import com.arduia.expense.feature.importexport.ui.ExportSettingsFlow
import com.arduia.expense.feature.importexport.ui.ImportDataFlow

interface ImportExportFeatureEntry {
    @Composable
    fun ExportFlow(
        onBack: () -> Unit,
        onExport: () -> Unit = {},
        modifier: Modifier = Modifier,
    )

    @Composable
    fun ImportFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )

    @Composable
    fun ClearDataFlow(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class ImportExportFeatureEntryImpl : ImportExportFeatureEntry {
    @Composable
    override fun ExportFlow(
        onBack: () -> Unit,
        onExport: () -> Unit,
        modifier: Modifier,
    ) {
        ExportSettingsFlow(onBack = onBack, onExport = onExport, modifier = modifier)
    }

    @Composable
    override fun ImportFlow(
        onBack: () -> Unit,
        modifier: Modifier,
    ) {
        ImportDataFlow(onBack = onBack, modifier = modifier)
    }

    @Composable
    override fun ClearDataFlow(
        onBack: () -> Unit,
        modifier: Modifier,
    ) {
        ClearDataFlow(onBack = onBack, modifier = modifier)
    }
}

object ImportExportFeatureUi : ImportExportFeatureEntry by ImportExportFeatureEntryImpl()
