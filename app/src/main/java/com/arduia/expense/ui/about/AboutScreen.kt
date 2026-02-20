package com.arduia.expense.ui.about

import android.content.res.Configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.about.molecule.AboutEvent
import com.arduia.expense.ui.about.molecule.AboutState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    state: AboutState,
    onEvent: (AboutEvent) -> Unit,
    versionName: String = "1.0.0",
    onNavigationIconClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onOpenSourceClick: () -> Unit = {},
    onContributeClick: () -> Unit = {},
    onDeveloperClick: () -> Unit = {},
    onInstallUpdateClick: () -> Unit = {}
) {
    if (state.updateInfo != null) {
        AboutUpdateDialog(
            data = state.updateInfo,
            onDismiss = { onEvent(AboutEvent.ClearUpdateInfo) },
            onInstallClick = onInstallUpdateClick
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = R.string.about)
                    )
                },
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
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(32.dp), // @dimen/grid_4 approx or grid_2 margin in XML
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )

                // App Name Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp, // Headline6 approx
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.beta_with_parenthness),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Version
                Text(
                    text = versionName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // darker_gray
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Update Status
                if (state.isNewVersionAvailable) {
                    AboutListItem(
                        text = stringResource(id = R.string.new_version_available),
                        onClick = { onEvent(AboutEvent.OpenUpdateInfo) }
                    )
                }

                // Description
                Text(
                    text = stringResource(id = R.string.app_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start, // XML says centerHorizontal parent but textAlignment not set strict
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(32.dp))

                // List Items
                AboutListItem(
                    text = stringResource(id = R.string.privacy_policy),
                    onClick = onPrivacyClick
                )

                AboutListItem(
                    text = stringResource(id = R.string.open_source_lib),
                    onClick = onOpenSourceClick
                )

                AboutListItem(
                    text = stringResource(id = R.string.contribute_app),
                    onClick = onContributeClick
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                // Acknowledgement Title
                Text(
                    text = stringResource(id = R.string.acknowledgement_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 32.dp)
                )

                // Acknowledgement Body
                Text(
                    text = stringResource(id = R.string.about_acknowledgement),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Developer
                Text(
                    text = stringResource(id = R.string.developer),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .padding(top = 50.dp, bottom = 32.dp)
                        .clickable(onClick = onDeveloperClick)
                )
            }
        }
    }
}

@Composable
fun AboutListItem(
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall // Subtitle1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AboutUpdateDialog(
    data: AboutUpdateUiModel,
    onDismiss: () -> Unit,
    onInstallClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.about_update)) },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_update),
                contentDescription = null
            )
        },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data.versionName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = data.versionCode,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = data.changeLogs,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onInstallClick()
                onDismiss() // Dismiss after click? Usually yes or wait. Logic says open link so dismiss ok.
            }) {
                Text(text = stringResource(id = R.string.install))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    ProExpenseTheme {
        AboutScreen(
            state = AboutState(),
            onEvent = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AboutScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        AboutScreen(
            state = AboutState(),
            onEvent = {}
        )
    }
}
