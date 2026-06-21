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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.categories.CategoryListFlow
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.preview.MoreSettingKind
import com.arduia.expense.ui.reports.ReportsFlow
import com.arduia.expense.ui.preview.previewMoreClearOptions
import com.arduia.expense.ui.preview.previewMoreCurrencies
import com.arduia.expense.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

private enum class MoreStep { Hub, Currency, Export, Clear, Reports, Categories }

@Composable
fun MoreFlow(
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSharedClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPinClick: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var step by remember { mutableStateOf(MoreStep.Hub) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var clearChecked by remember { mutableStateOf(setOf("expenses")) }
    var showClearConfirm by remember { mutableStateOf(false) }

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
                MoreStep.Currency -> MoreCurrencyScreen(
                    items = previewMoreCurrencies,
                    selectedCode = selectedCurrency,
                    onSelect = { selectedCurrency = it },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Export -> MoreExportScreen(
                    files = previewMoreExportFiles,
                    onExport = {},
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Clear -> MoreClearScreen(
                    options = previewMoreClearOptions,
                    checkedIds = clearChecked,
                    onToggle = { id ->
                        clearChecked = if (id in clearChecked) clearChecked - id else clearChecked + id
                    },
                    onClear = { showClearConfirm = true },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Reports -> ReportsFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Categories -> CategoryListFlow(
                    onBack = { step = MoreStep.Hub },
                )
            }
        }

        ProAlertDialog(
            visible = showClearConfirm,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.more_clear_confirm_title),
            body = buildAnnotatedString { append(stringResource(R.string.more_clear_confirm_body)) },
            confirmLabel = stringResource(R.string.more_clear_confirm_action),
            onConfirm = {
                showClearConfirm = false
                clearChecked = emptySet()
                step = MoreStep.Hub
            },
            dismissLabel = stringResource(R.string.more_clear_confirm_cancel),
            onDismiss = { showClearConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )
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
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
            onDebtClick = {},
            onSharedClick = {},
        )
    }
}
