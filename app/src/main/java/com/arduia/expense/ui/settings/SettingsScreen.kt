package com.arduia.expense.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.form.SettingsRow
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun SettingsScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToCurrency: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onNavigateToCurrency = onNavigateToCurrency,
        onNavigateToLanguage = onNavigateToLanguage,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToFeedback = onNavigateToFeedback,
        onThemeClick = viewModel::onThemeClick,
        modifier = modifier,
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onOpenDrawer: () -> Unit,
    onNavigateToCurrency: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Settings",
                navIcon = TopBarNavIcon.Drawer,
                onNavIconClick = onOpenDrawer,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                SettingsRow(
                    title = "Currency",
                    subtitle = uiState.currentCurrency,
                    onClick = onNavigateToCurrency,
                )
            }
            item {
                SettingsRow(
                    title = "Language",
                    subtitle = uiState.currentLanguage,
                    onClick = onNavigateToLanguage,
                )
            }
            item {
                SettingsRow(
                    title = "Theme",
                    subtitle = uiState.currentTheme.name,
                    onClick = onThemeClick,
                )
            }
            item {
                SettingsRow(
                    title = "Feedback",
                    onClick = onNavigateToFeedback,
                )
            }
            item {
                SettingsRow(
                    title = "About",
                    subtitle = uiState.appVersion,
                    onClick = onNavigateToAbout,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsContent() {
    ProExpenseTheme {
        SettingsContent(
            uiState = SettingsUiState(
                currentCurrency = "USD – US Dollar",
                currentLanguage = "English",
                appVersion = "v1.0.0-beta08",
            ),
            onOpenDrawer = {},
            onNavigateToCurrency = {},
            onNavigateToLanguage = {},
            onNavigateToAbout = {},
            onNavigateToFeedback = {},
            onThemeClick = {},
        )
    }
}
