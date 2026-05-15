package com.arduia.expense.ui.component.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class BackupRowUiModel(
    val id: Long,
    val name: String,
    val date: String,
    val isInProgress: Boolean,
)

@Composable
fun BackupRow(
    item: BackupRowUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.isInProgress) {
            CircularProgressIndicator()
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Completed",
                tint = ProExpenseTheme.colors.positive,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBackupRowCompleted() {
    ProExpenseTheme {
        BackupRow(
            item = BackupRowUiModel(
                id = 1L,
                name = "expense_backup_2026_05.xls",
                date = "May 15, 2026",
                isInProgress = false,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewBackupRowInProgress() {
    ProExpenseTheme {
        BackupRow(
            item = BackupRowUiModel(
                id = 2L,
                name = "expense_backup_2026_06.xls",
                date = "May 15, 2026",
                isInProgress = true,
            ),
        )
    }
}
