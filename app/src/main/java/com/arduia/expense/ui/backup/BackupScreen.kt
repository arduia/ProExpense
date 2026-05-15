package com.arduia.expense.ui.backup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.state.NoDataPlaceholder
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.backup.BackupRow
import com.arduia.expense.ui.component.dialog.ExportDialog
import com.arduia.expense.ui.component.dialog.ExportUiState

@Composable
fun BackupScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackupContent(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onImportClick = viewModel::onImportClick,
        onExportClick = viewModel::onExportClick,
        onExportRangeSelect = viewModel::onExportRangeSelect,
        onExportConfirm = viewModel::onExportConfirm,
        onExportDialogDismiss = viewModel::onExportDialogDismiss,
        modifier = modifier,
    )
}

@Composable
private fun BackupContent(
    uiState: BackupUiState,
    onOpenDrawer: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onExportRangeSelect: (String) -> Unit,
    onExportConfirm: () -> Unit,
    onExportDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Backup",
                navIcon = TopBarNavIcon.Drawer,
                onNavIconClick = onOpenDrawer,
            )
        },
    ) { innerPadding ->
        if (uiState.backupFiles.isEmpty()) {
            NoDataPlaceholder(
                message = "No backup files yet",
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(uiState.backupFiles, key = { it.id }) { file ->
                    BackupRow(item = file)
                }
            }
        }

        if (uiState.showExportDialog) {
            ExportDialog(
                state = ExportUiState(
                    selectedRange = "Month",
                    availableRanges = listOf("Week", "Month", "Year", "All"),
                ),
                onRangeSelect = onExportRangeSelect,
                onExport = onExportConfirm,
                onDismiss = onExportDialogDismiss,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBackupContent() {
    ProExpenseTheme {
        BackupContent(
            uiState = BackupUiState(),
            onOpenDrawer = {},
            onImportClick = {},
            onExportClick = {},
            onExportRangeSelect = {},
            onExportConfirm = {},
            onExportDialogDismiss = {},
        )
    }
}
