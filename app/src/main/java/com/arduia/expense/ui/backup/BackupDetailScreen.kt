package com.arduia.expense.ui.backup

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun BackupDetailScreen(
    backupName: String,
    totalItems: String,
    isLoading: Boolean = false,
    onCloseClick: () -> Unit = {},
    onImportClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.backup_details),
                    style = MaterialTheme.typography.titleLarge, // Headline6
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drop_down),
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Label
            Text(
                text = stringResource(id = R.string.name),
                style = MaterialTheme.typography.labelSmall, // Overline
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Name Value
            Text(
                text = backupName,
                style = MaterialTheme.typography.bodyLarge, // Body1
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total Items Label
            Text(
                text = stringResource(id = R.string.total),
                style = MaterialTheme.typography.bodyLarge, // Body1 (from XML, though structure suggests Label)
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Total Items Value
            Text(
                text = totalItems,
                style = MaterialTheme.typography.bodyLarge, // Body1
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Import Button
            ProExpenseButton(
                text = stringResource(id = R.string.import_data),
                onClick = onImportClick,
                icon = painterResource(id = R.drawable.ic_import),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackupDetailScreenPreview() {
    ProExpenseTheme {
        BackupDetailScreen(
            backupName = "Backup_expense_2023_12_01",
            totalItems = "150",
            isLoading = false
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BackupDetailScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        BackupDetailScreen(
            backupName = "Backup_expense_2023_12_01",
            totalItems = "150",
            isLoading = true
        )
    }
}
