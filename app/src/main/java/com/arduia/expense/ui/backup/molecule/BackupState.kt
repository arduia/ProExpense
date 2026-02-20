package com.arduia.expense.ui.backup.molecule

import android.net.Uri
import com.arduia.expense.ui.backup.BackupUiModel

data class BackupState(
    val backupList: List<BackupUiModel> = emptyList(),
    val isEmptyBackupLogs: Boolean = true,
    val isEmptyExpenseLogs: Boolean = true,
    val backupFilePath: Uri? = null,
    val showDeleteConfirmFor: BackupUiModel? = null,
    val showExportDialog: Boolean = false,
    val importUri: Uri? = null
)
