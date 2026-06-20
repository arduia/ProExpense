package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proCardSurface
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.MoreHubUiState
import com.arduia.expense.ui.preview.previewMoreHubState
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreHubScreenContent(
    state: MoreHubUiState,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSharedCostsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onClearClick: () -> Unit,
    onAddClick: () -> Unit,
    onTabSelected: (HomeNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val budgetValue = if (state.budgetEnabled) {
        stringResource(R.string.on)
    } else {
        stringResource(R.string.off)
    }
    val pinValue = if (state.pinEnabled) {
        stringResource(R.string.on)
    } else {
        stringResource(R.string.off)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = dimens.space18,
                    end = dimens.space18,
                    top = dimens.space24,
                    bottom = if (state.showBottomNav) {
                        dimens.space24 + dimens.navShellBottomInset
                    } else {
                        dimens.space24
                    },
                ),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Text(
                text = stringResource(R.string.more_hub_eyebrow),
                style = typography.eyebrow,
                color = colors.muted,
            )
            Text(
                text = stringResource(R.string.more_hub),
                style = typography.screenTitle,
                color = colors.onSurface,
            )

            MoreHubProfileCard(
                name = state.profileName,
                initial = state.profileInitial,
            )

            MoreHubSectionLabel(text = stringResource(R.string.more_features_section))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .proCardSurface(),
            ) {
                MoreHubFeatureRow(
                    title = stringResource(R.string.more_reports),
                    icon = ProIconGlyph.FeatReports,
                    iconTint = colors.primaryDeep,
                    iconBackground = colors.primaryTint,
                    onClick = onReportsClick,
                )
                MoreHubDivider()
                MoreHubFeatureRow(
                    title = stringResource(R.string.more_debt),
                    icon = ProIconGlyph.FeatDebt,
                    iconTint = colors.primaryDeep,
                    iconBackground = colors.primaryTint,
                    onClick = onDebtClick,
                )
                MoreHubDivider()
                MoreHubFeatureRow(
                    title = stringResource(R.string.more_shared_costs),
                    icon = ProIconGlyph.FeatSplit,
                    iconTint = colors.primaryDeep,
                    iconBackground = colors.primaryTint,
                    onClick = onSharedCostsClick,
                )
                MoreHubDivider()
                MoreHubFeatureRow(
                    title = stringResource(R.string.more_categories),
                    icon = ProIconGlyph.CatDefault,
                    iconTint = colors.onSurface,
                    iconBackground = colors.paperAlt,
                    onClick = onCategoriesClick,
                )
            }

            MoreHubSectionLabel(text = stringResource(R.string.more_settings_section))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .proCardSurface(),
            ) {
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_currency),
                    value = state.currencyCode,
                    icon = ProIconGlyph.Sparkle,
                    onClick = onCurrencyClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_monthly_budget),
                    value = budgetValue,
                    icon = ProIconGlyph.Budget,
                    onClick = onBudgetClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_security),
                    value = pinValue,
                    icon = ProIconGlyph.Eye,
                    onClick = onSecurityClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_language),
                    value = state.languageLabel,
                    icon = ProIconGlyph.Note,
                    onClick = onLanguageClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_export),
                    value = null,
                    icon = ProIconGlyph.Note,
                    onClick = onExportClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.import_data),
                    value = null,
                    icon = ProIconGlyph.Note,
                    onClick = onImportClick,
                )
                MoreHubDivider()
                MoreHubSettingsRow(
                    title = stringResource(R.string.more_clear),
                    value = null,
                    icon = ProIconGlyph.Close,
                    iconTint = colors.danger,
                    iconBackground = colors.dangerTint,
                    onClick = onClearClick,
                )
            }
        }

        if (state.showBottomNav) {
            HomeBottomNav(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                selectedTab = state.activeTab,
                onTabSelected = onTabSelected,
                onAddClick = onAddClick,
            )
        }
    }
}

@Composable
private fun MoreHubSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = ProExpenseTheme.typography.eyebrow,
        color = ProExpenseTheme.colors.muted,
        modifier = modifier,
    )
}

@Composable
private fun MoreHubProfileCard(
    name: String,
    initial: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proCardSurface()
            .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.space44)
                .clip(ProExpenseTheme.shapes.tile)
                .background(colors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = typography.bodySemiBold,
                color = colors.primaryDeep,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space2)) {
            Text(
                text = name,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.more_profile_subtitle),
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
    }
}

@Composable
private fun MoreHubFeatureRow(
    title: String,
    icon: ProIconGlyph,
    iconTint: androidx.compose.ui.graphics.Color,
    iconBackground: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.quickAccessIconSize)
                .clip(ProExpenseTheme.shapes.tile)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = icon,
                contentDescription = null,
                tint = iconTint,
                size = dimens.iconInline + 4.dp,
            )
        }
        Text(
            text = title,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Composable
private fun MoreHubSettingsRow(
    title: String,
    value: String?,
    icon: ProIconGlyph,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: androidx.compose.ui.graphics.Color = ProExpenseTheme.colors.onSurface,
    iconBackground: androidx.compose.ui.graphics.Color = ProExpenseTheme.colors.paperAlt,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.quickAccessIconSize)
                .clip(ProExpenseTheme.shapes.tile)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = icon,
                contentDescription = null,
                tint = iconTint,
                size = dimens.iconInline + 4.dp,
            )
        }
        Text(
            text = title,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            Text(
                text = value,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Composable
private fun MoreHubDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = ProExpenseTheme.dimensions.space14 + ProExpenseTheme.dimensions.quickAccessIconSize + 12.dp),
        thickness = 1.dp,
        color = ProExpenseTheme.colors.line,
    )
}

@Preview(
    name = "More hub — handoff",
    widthDp = ProArtboard.HANDOFF_WIDTH_DP,
    heightDp = ProArtboard.HANDOFF_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreHubPreview() {
    ProExpenseTheme {
        MoreHubScreenContent(
            state = previewMoreHubState,
            onReportsClick = {},
            onDebtClick = {},
            onSharedCostsClick = {},
            onCategoriesClick = {},
            onCurrencyClick = {},
            onBudgetClick = {},
            onSecurityClick = {},
            onLanguageClick = {},
            onExportClick = {},
            onImportClick = {},
            onClearClick = {},
            onAddClick = {},
            onTabSelected = {},
        )
    }
}
