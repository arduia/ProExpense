package com.arduia.expense.ui.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.ui.FeatureUiRegistry
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

private enum class MoreStep { Hub, Currency, Export, Clear, Reports, Categories }

@Composable
fun MoreFlow(
    features: FeatureUiRegistry,
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSharedClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPinClick: () -> Unit = {},
    selectedCurrency: String = "USD",
    onCurrencySelect: (String) -> Unit = {},
    reportsPeriods: List<ReportsUiState>? = null,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var step by remember { mutableStateOf(MoreStep.Hub) }

    val hubState = remember(selectedCurrency) {
        previewMoreHub.copy(
            settings = previewMoreHub.settings.map { setting ->
                if (setting.id == "currency") setting.copy(value = selectedCurrency) else setting
            },
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = initialState.ordinal,
                    toIndex = targetState.ordinal,
                    reduceMotion = reduceMotion,
                )
            },
            label = "moreStep",
        ) { target ->
            when (target) {
                MoreStep.Hub -> MoreHubScreen(
                    state = hubState,
                    onFeatureClick = { id ->
                        when (id) {
                            "debt" -> onDebtClick()
                            "shared" -> onSharedClick()
                            "reports" -> step = MoreStep.Reports
                            "categories" -> step = MoreStep.Categories
                        }
                    },
                    onSettingClick = { id ->
                        when (id) {
                            "currency" -> step = MoreStep.Currency
                            "export" -> step = MoreStep.Export
                            "clear" -> step = MoreStep.Clear
                            "pin" -> onPinClick()
                        }
                    },
                    onSettingToggle = { _, _ -> },
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                )
                MoreStep.Currency -> features.currency.SettingsFlow(
                    selectedCode = selectedCurrency,
                    onSelect = onCurrencySelect,
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Export -> features.importExport.ExportFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Clear -> features.importExport.ClearDataFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Reports -> features.reports.ReportsFlow(
                    onBack = { step = MoreStep.Hub },
                    empty = reportsPeriods?.isEmpty() ?: false,
                    periods = reportsPeriods,
                )
                MoreStep.Categories -> features.categories.CategoryListFlow(
                    onBack = { step = MoreStep.Hub },
                )
            }
        }
    }
}

@Preview(
    name = "More flow — hub",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreFlowPreview() {
    ProExpenseTheme {
        MoreFlow(
            features = FeatureUiRegistry(),
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
            onDebtClick = {},
            onSharedClick = {},
        )
    }
}
