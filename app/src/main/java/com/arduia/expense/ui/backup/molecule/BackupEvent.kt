package com.arduia.expense.ui.backup.molecule

import android.net.Uri
import com.arduia.expense.ui.backup.BackupUiModel

sealed interface BackupEvent {
    object ExportClicked : BackupEvent
    object ExportDialogDismissed : BackupEvent
    object ImportClicked : BackupEvent
    data class ImportUriReceived(val uri: Uri) : BackupEvent
    object ImportDialogDismissed : BackupEvent
    data class DeleteIconClicked(val item: BackupUiModel) : BackupEvent
    object DeleteConfirmDismissed : BackupEvent
    data class DeleteConfirmed(val item: BackupUiModel) : BackupEvent
}
