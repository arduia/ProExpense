package com.arduia.expense.ui.settings

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.settings.molecule.SettingsEvent
import com.arduia.expense.ui.settings.molecule.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigationIconClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onOpenUpdateInfoClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
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
                    .widthIn(max = 450.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 16.dp), // @dimen/grid_2
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Language Item
                SettingsItem(
                    title = stringResource(id = R.string.language),
                    onClick = onLanguageClick,
                    trailingContent = {
                        Image(
                            painter = painterResource(id = R.drawable.flag_myanmar),
                            contentDescription = "Language",
                            modifier = Modifier
                                .width(60.dp)
                                .height(25.dp)
                        )
                    }
                )

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Currency Item
                SettingsItem(
                    title = stringResource(id = R.string.currency),
                    onClick = onCurrencyClick,
                    trailingContent = {
                        Text(
                            text = state.currencyValue,
                            style = MaterialTheme.typography.titleLarge, // Headline6 approx
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                )

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Theme Item
                SettingsItem(
                    title = stringResource(id = R.string.theme_mode),
                    onClick = { onEvent(SettingsEvent.ChooseThemeClicked) },
                    trailingContent = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_theme),
                            contentDescription = "Theme",
                            modifier = Modifier
                                .width(60.dp)
                                .height(24.dp) // standard_icon_size approx
                                .padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                )

                if (state.isNewVersionAvailable) {
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    SettingsItem(
                        title = stringResource(id = R.string.about_update),
                        onClick = onOpenUpdateInfoClick,
                        trailingContent = {
                            Text(
                                text = "New",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    )
                }
            }
        }

        if (state.showThemeDialogForMode != null) {
            Dialog(onDismissRequest = { onEvent(SettingsEvent.ThemeDialogDismissed) }) {
                ChooseThemeDialogScreen(
                    currentMode = state.showThemeDialogForMode,
                    onModeSelected = { mode -> onEvent(SettingsEvent.ThemeSelected(mode)) },
                    onDismiss = { onEvent(SettingsEvent.ThemeDialogDismissed) }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp), // @dimen/grid_3 horizontal, grid_2 vertical approx
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        trailingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ProExpenseTheme {
        SettingsScreen(
            state = SettingsState(currencyValue = "$"),
            onEvent = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        SettingsScreen(
            state = SettingsState(currencyValue = "$"),
            onEvent = {}
        )
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun SettingsScreenBurmesePreview() {
    ProExpenseTheme {
        SettingsScreen(
            state = SettingsState(currencyValue = "Ks"),
            onEvent = {}
        )
    }
}
