package com.arduia.expense.ui.more

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.design.ProExpenseCard
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpensePalette
import com.arduia.expense.ui.theme.ProExpenseTheme

/** Destinations reachable from the More hub. */
enum class MoreDestination {
    Reports,
    DebtTracker,
    SharedCosts,
    Categories,
    Currency,
    MonthlyBudget,
    PinAuth,
    Language,
    Theme,
    Export,
    ClearData,
}

/** Dynamic values shown on the hub. */
data class MoreHubState(
    val profileName: String = "Maya",
    val profileSubtitle: String = "All data local · no account",
    val currencyCode: String = "USD",
    val monthlyBudget: String = "Off",
    val pinAuth: String = "Off",
    val language: String = "English",
    val theme: String = "System",
    val version: String = "v0.1.0 · MVP",
)

@Composable
fun MoreHubScreen(
    onNavigate: (MoreDestination) -> Unit,
    onTabSelect: (BottomTab) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    state: MoreHubState = MoreHubState(),
) {
    val colors = ProExpenseTheme.colors
    val featureTint = colors.primaryTint
    val featureAccent = colors.primary
    val settingTint = colors.paper
    val settingAccent = colors.ink3

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoreHorizontalPadding)
                .padding(top = 8.dp, bottom = 4.dp),
        ) {
            Text(
                text = "SETTINGS & EXTRAS",
                style = ProExpenseTheme.typography.eyebrow,
                color = colors.ink3,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "More",
                style = ProExpenseTheme.typography.screenTitle,
                color = colors.ink,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MoreHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            ProfileCard(name = state.profileName, subtitle = state.profileSubtitle)

            SettingsGroup(
                title = "Features",
                rows = listOf(
                    {
                        SettingsRow(
                            label = "Reports",
                            onClick = { onNavigate(MoreDestination.Reports) },
                            iconTint = featureTint,
                        ) { BarChartGlyph(color = featureAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Debt Tracker",
                            onClick = { onNavigate(MoreDestination.DebtTracker) },
                            iconTint = featureTint,
                        ) { CoinsGlyph(color = featureAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Shared Costs",
                            onClick = { onNavigate(MoreDestination.SharedCosts) },
                            iconTint = featureTint,
                        ) { SplitGlyph(color = featureAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Category List",
                            onClick = { onNavigate(MoreDestination.Categories) },
                            iconTint = featureTint,
                        ) { TagGlyph(color = featureAccent, modifier = it) }
                    },
                ),
            )

            SettingsGroup(
                title = "Settings",
                rows = listOf(
                    {
                        SettingsRow(
                            label = "Currency",
                            onClick = { onNavigate(MoreDestination.Currency) },
                            detail = state.currencyCode,
                            iconTint = settingTint,
                        ) { CirclePlusGlyph(color = settingAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Monthly budget",
                            onClick = { onNavigate(MoreDestination.MonthlyBudget) },
                            detail = state.monthlyBudget,
                            iconTint = settingTint,
                        ) { DocLinesGlyph(color = settingAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "PIN authentication",
                            onClick = { onNavigate(MoreDestination.PinAuth) },
                            detail = state.pinAuth,
                            iconTint = settingTint,
                        ) { EyeGlyph(color = settingAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Language",
                            onClick = { onNavigate(MoreDestination.Language) },
                            detail = state.language,
                            iconTint = settingTint,
                        ) { GlobeGlyph(color = settingAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Theme",
                            onClick = { onNavigate(MoreDestination.Theme) },
                            detail = state.theme,
                            iconTint = settingTint,
                        ) { ThemeGlyph(color = settingAccent, modifier = it) }
                    },
                ),
            )

            SettingsGroup(
                title = "Data",
                rows = listOf(
                    {
                        SettingsRow(
                            label = "Export data",
                            onClick = { onNavigate(MoreDestination.Export) },
                            detail = "CSV · JSON",
                            iconTint = featureTint,
                        ) { FileGlyph(color = featureAccent, modifier = it) }
                    },
                    {
                        SettingsRow(
                            label = "Clear data",
                            onClick = { onNavigate(MoreDestination.ClearData) },
                            danger = true,
                            iconTint = colors.danger.copy(alpha = 0.12f),
                        ) { TrashGlyph(color = colors.danger, modifier = it) }
                    },
                ),
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.version,
                    style = ProExpenseTheme.typography.monoCaption,
                    color = colors.muted,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        ProExpenseBottomNav(
            activeTab = BottomTab.More,
            onTabSelect = onTabSelect,
            onAdd = onAdd,
        )
    }
}

@Composable
private fun ProfileCard(
    name: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    ProExpenseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(letter = name.firstOrNull()?.uppercase() ?: "?")
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = ProExpenseTheme.typography.sectionHead,
                    color = colors.ink,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = ProExpenseTheme.typography.caption,
                    color = colors.muted,
                )
            }
        }
    }
}

@Composable
private fun Avatar(
    letter: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = ProExpensePalette.Blue100,
        contentColor = ProExpensePalette.Blue700,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter,
                style = ProExpenseTheme.typography.sectionHead.copy(fontSize = 22.sp),
                color = ProExpensePalette.Blue700,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun MoreHubScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            MoreHubScreen(
                onNavigate = {},
                onTabSelect = {},
                onAdd = {},
            )
        }
    }
}
