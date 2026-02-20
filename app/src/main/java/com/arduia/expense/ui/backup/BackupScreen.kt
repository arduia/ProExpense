package com.arduia.expense.ui.backup

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

import com.arduia.expense.ui.backup.molecule.BackupState
import com.arduia.expense.ui.backup.molecule.BackupEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    state: BackupState,
    onEvent: (BackupEvent) -> Unit,
    onNavigationIconClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.backup)) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp) // @dimen/width_layout_min
                    .fillMaxSize()
            ) {
                // Export/Import Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export Card
                    BackupActionCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_export,
                        text = stringResource(id = R.string.export),
                        onClick = { onEvent(BackupEvent.ExportClicked) },
                        enabled = !state.isEmptyExpenseLogs
                    )

                    // Import Card
                    BackupActionCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_import,
                        text = stringResource(id = R.string.import_data),
                        onClick = { onEvent(BackupEvent.ImportClicked) },
                        enabled = true
                    )
                }

                // Backup Logs Title
                Text(
                    text = stringResource(id = R.string.backup_logs),
                    style = MaterialTheme.typography.titleMedium, // MediumTitle
                    modifier = Modifier
                        .padding(start = 16.dp, top = 32.dp, bottom = 4.dp) // @dimen/grid_3, grid_4, grid_1
                )

                if (state.isEmptyBackupLogs) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_data),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.backupList) { backup ->
                            BackupLogItem(
                                backup = backup,
                                onDeleteClick = { onEvent(BackupEvent.DeleteIconClicked(backup)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupActionCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.5f
    ProExpenseCard(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick, enabled = enabled),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                )
                
                Spacer(modifier = Modifier.height(16.dp)) // @dimen/grid_3

                // Text
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
            }
        }
    }
}

@Composable
fun BackupLogItem(
    backup: BackupUiModel,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { } // Item click not specified in XML, but usually list items are clickable.
            .padding(horizontal = 16.dp, vertical = 8.dp) // @dimen/grid_3
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp) // Space for delete icon
        ) {
            // Backup Name
            Text(
                text = backup.name,
                style = MaterialTheme.typography.bodyLarge, // Body1
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp)) // @dimen/grid_2

            Row(verticalAlignment = Alignment.Bottom) {
                // Date
                Text(
                    text = backup.date,
                    style = MaterialTheme.typography.labelSmall, // Caption
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.width(16.dp)) // space between date and items

                // Items Count
                Text(
                    text = backup.items,
                    style = MaterialTheme.typography.titleSmall, // Subtitle2
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Progress
                if (backup.onProgress) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp)) // @dimen/grid_3 bottom margin in XML on Date
        }

        // Delete Icon
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(35.dp), // @dimen/standard_icon_size
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackupScreenPreview() {
    val mockData = listOf(
        BackupUiModel("Backup_2023_10_27.csv", 1, "27 Oct 2023", "150 items", false),
        BackupUiModel("Backup_2023_09_15.csv", 2, "15 Sep 2023", "120 items", true),
        BackupUiModel("Backup_2023_08_01.csv", 3, "01 Aug 2023", "90 items", false)
    )
    ProExpenseTheme {
        BackupScreen(state = BackupState(backupList = mockData, isEmptyBackupLogs = false), onEvent = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BackupScreenDarkPreview() {
     val mockData = listOf(
        BackupUiModel("Backup_2023_10_27.csv", 1, "27 Oct 2023", "150 items", false),
        BackupUiModel("Backup_2023_09_15.csv", 2, "15 Sep 2023", "120 items", true)
    )
    ProExpenseTheme(darkTheme = true) {
        BackupScreen(state = BackupState(backupList = mockData, isEmptyBackupLogs = false), onEvent = {})
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun BackupScreenBurmesePreview() {
     val mockData = listOf(
        BackupUiModel("Backup_2023_10_27.csv", 1, "27 Oct 2023", "150 items", false)
    )
    ProExpenseTheme {
        BackupScreen(state = BackupState(backupList = mockData, isEmptyBackupLogs = false), onEvent = {})
    }
}
