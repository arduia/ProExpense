package com.arduia.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.ui.res.stringResource

@Composable
fun BackupItem(
    name: String,
    date: String,
    itemCount: String,
    isSyncing: Boolean = false,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Using Row/Column directly instead of StandardCard for list items usually, 
    // but preserving the "clickable background" feel.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = itemCount,
                    style = MaterialTheme.typography.titleSmall, // Using titleSmall/subtitle2
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        if (isSyncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).padding(end = 8.dp),
                strokeWidth = 2.dp
            )
        } else {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = stringResource(id = R.string.desc_delete_backup),
                    tint = MaterialTheme.colorScheme.error // or custom delete color
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBackupItem() {
    ProExpenseTheme {
        BackupItem(
            name = "Backup_2023_10_24.csv",
            date = "Oct 24, 2023",
            itemCount = "42 items",
            onItemClick = {},
            onDeleteClick = {}
        )
    }
}
