package com.arduia.expense.ui.backup

import com.arduia.expense.ui.component.backup.BackupRowUiModel

data class BackupUiState(
    val backupFiles: List<BackupRowUiModel> = emptyList(),
    val isImporting: Boolean = false,
    val isExporting: Boolean = false,
    val showExportDialog: Boolean = false,
)

sealed interface BackupUiEvent {
    data object ImportSuccess : BackupUiEvent
    data object ExportSuccess : BackupUiEvent
    data class ShowError(val message: String) : BackupUiEvent
}
