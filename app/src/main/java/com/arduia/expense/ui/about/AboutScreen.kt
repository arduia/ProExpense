package com.arduia.expense.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
    appVersion: String = "",
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "About",
                navIcon = TopBarNavIcon.Back,
                onNavIconClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(ProExpenseTheme.spacing.grid3),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        ) {
            Text(
                text = "Pro Expense",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = appVersion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "A free, open-source, privacy-focused expense tracking app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAboutScreen() {
    ProExpenseTheme {
        AboutScreen(
            onNavigateBack = {},
            onNavigateToPrivacyPolicy = {},
            appVersion = "v1.0.0-beta08",
        )
    }
}
