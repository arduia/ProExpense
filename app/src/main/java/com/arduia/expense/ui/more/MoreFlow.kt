package com.arduia.expense.ui.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.R as AuthR
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.ui.FeatureUiRegistry
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.arduia.expense.data.BudgetRepository

private enum class MoreStep { Hub, Currency, Export, Clear, Reports, Categories, Budget }

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
    pinConfigured: Boolean? = null,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileRepository: ProfileRepository = koinInject()
    val currencyRepository: CurrencyRepository = koinInject()
    val pinAuthRepository: PinAuthRepository = koinInject()
    val budgetRepository: BudgetRepository = koinInject()

    var step by remember { mutableStateOf(MoreStep.Hub) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var displayName by remember { mutableStateOf("") }
    var pinEnabled by remember { mutableStateOf(false) }
    var showDisablePinConfirm by remember { mutableStateOf(false) }
    var monthlyBudgetLabel by remember { mutableStateOf("Off") }
    var appVersion by remember { mutableStateOf("1.0.0") }
    var homeCurrencyCode by remember { mutableStateOf(CurrencyCode("USD")) }

    LaunchedEffect(pinConfigured) {
        if (pinConfigured != null) pinEnabled = pinConfigured
    }

    LaunchedEffect(Unit) {
        // Load display name
        when (val result = profileRepository.getDisplayName()) {
            is Result.Success -> displayName = result.data
            is Result.Error -> Unit
        }
        // Load home currency
        when (val result = currencyRepository.getSettings()) {
            is Result.Success -> {
                selectedCurrency = result.data.homeCurrency.code
                homeCurrencyCode = result.data.homeCurrency
            }
            is Result.Error -> Unit
        }
        // Load PIN status
        when (val result = pinAuthRepository.isPinConfigured()) {
            is Result.Success -> pinEnabled = result.data
            is Result.Error -> Unit
        }
        // Load monthly budget
        when (val result = budgetRepository.getMonthlyBudget()) {
            is Result.Success -> {
                result.data?.let { budget ->
                    monthlyBudgetLabel = "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", budget.amount.valueInCents / 100.0))
                } ?: run {
                    monthlyBudgetLabel = "Off"
                }
            }
            is Result.Error -> monthlyBudgetLabel = "Off"
        }
        // Load app version
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersion = packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            appVersion = "1.0.0"
        }
    }

    val hubState = remember(selectedCurrency, displayName, pinEnabled, monthlyBudgetLabel, appVersion) {
        val profileInitial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "M"
        previewMoreHub.copy(
            profile = previewMoreHub.profile.copy(
                initial = profileInitial,
                name = displayName.ifBlank { previewMoreHub.profile.name },
            ),
            settings = previewMoreHub.settings.map { setting ->
                when (setting.id) {
                    "currency" -> setting.copy(value = selectedCurrency)
                    "pin" -> setting.copy(value = if (pinEnabled) "On" else "Off")
                    "budget" -> setting.copy(value = monthlyBudgetLabel)
                    "version" -> setting.copy(value = appVersion)
                    else -> setting
                }
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
                            "pin" -> if (pinEnabled) showDisablePinConfirm = true else onPinClick()
                            "budget" -> step = MoreStep.Budget
                        }
                    },
                    onSettingToggle = { _, _ -> },
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                )
                MoreStep.Currency -> features.currency.SettingsFlow(
                    selectedCode = selectedCurrency,
                    onSelect = { newCode ->
                        selectedCurrency = newCode
                        scope.launch {
                            currencyRepository.setHomeCurrency(CurrencyCode(newCode))
                            homeCurrencyCode = CurrencyCode(newCode)
                        }
                    },
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
                )
                MoreStep.Categories -> features.categories.CategoryListFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Budget -> MoreBudgetScreen(
                    currentAmount = monthlyBudgetLabel.takeIf { it != "Off" },
                    homeCurrency = homeCurrencyCode,
                    onSave = { money ->
                        scope.launch {
                            budgetRepository.setMonthlyBudget(money)
                            monthlyBudgetLabel = if (money != null) {
                                "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", money.amount.valueInCents / 100.0))
                            } else {
                                "Off"
                            }
                            step = MoreStep.Hub
                        }
                    },
                    onBack = { step = MoreStep.Hub },
                )
            }
        }

        ProAlertDialog(
            visible = showDisablePinConfirm,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(AuthR.string.pin_disable_confirm_title),
            body = buildAnnotatedString { append(stringResource(AuthR.string.pin_disable_confirm_body)) },
            confirmLabel = stringResource(AuthR.string.pin_disable_confirm_action),
            onConfirm = {
                scope.launch {
                    pinAuthRepository.clearPin()
                    pinEnabled = false
                    showDisablePinConfirm = false
                }
            },
            dismissLabel = stringResource(AuthR.string.pin_disable_confirm_cancel),
            onDismiss = { showDisablePinConfirm = false },
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
            features = FeatureUiRegistry(),
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
            onDebtClick = {},
            onSharedClick = {},
        )
    }
}
