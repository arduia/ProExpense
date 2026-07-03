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
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.feature.auth.BiometricAuthenticator
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.R as AuthR
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.currency.SaveHomeCurrencyUseCase
import com.arduia.expense.ui.FeatureUiRegistry
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.preview.previewMoreHub
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.R

private enum class MoreStep { Hub, Currency, Export, Import, Clear, Reports, Categories, Budget, DefaultCategory, Theme, Language }

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
    onCurrencyChanged: (CurrencyCode) -> Unit = {},
    onBudgetChanged: (Money?) -> Unit = {},
    onDefaultCategoryChanged: (String) -> Unit = {},
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    onLanguageChanged: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()
    val profileRepository: ProfileRepository = koinInject()
    val currencyRepository: CurrencyRepository = koinInject()
    val saveHomeCurrency: SaveHomeCurrencyUseCase = koinInject()
    val themeRepository: ThemeRepository = koinInject()
    val localeRepository: LocaleRepository = koinInject()
    val pinAuthRepository: PinAuthRepository = koinInject()
    val disablePin: DisablePinUseCase = koinInject()
    val budgetRepository: BudgetRepository = koinInject()
    val defaultCategoryRepository: DefaultCategoryRepository = koinInject()
    val profileNameFallback = stringResource(R.string.more_profile_name_fallback)
    val themeLightLabel = stringResource(R.string.theme_light)
    val themeDarkLabel = stringResource(R.string.theme_dark)
    val themeSystemLabel = stringResource(R.string.theme_system)

    var step by remember { mutableStateOf(MoreStep.Hub) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var displayName by remember { mutableStateOf("") }
    var pinEnabled by remember { mutableStateOf(false) }
    var showPinManageSheet by remember { mutableStateOf(false) }
    var showDisablePinConfirm by remember { mutableStateOf(false) }
    var showDisablePinVerify by remember { mutableStateOf(false) }
    var showPinChangeFlow by remember { mutableStateOf(false) }
    var monthlyBudgetLabel by remember { mutableStateOf("Off") }
    var appVersion by remember { mutableStateOf("1.0.0") }
    var homeCurrencyCode by remember { mutableStateOf(CurrencyCode("USD")) }
    var biometricEnrolled by remember { mutableStateOf(false) }
    val biometricCapable = activity != null && BiometricAuthenticator.isAvailable(activity)
    var defaultCategoryId by remember { mutableStateOf("food") }
    var themeMode by remember { mutableStateOf(ThemeMode.DARK) }
    var languageTag by remember { mutableStateOf(AppLanguage.DEFAULT.tag) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

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
        // Load biometric enrollment
        when (val result = pinAuthRepository.isBiometricEnrolled()) {
            is Result.Success -> biometricEnrolled = result.data
            is Result.Error -> Unit
        }
        // Load monthly budget
        when (val result = budgetRepository.getMonthlyBudget()) {
            is Result.Success -> {
                result.data?.let { budget ->
                    monthlyBudgetLabel = AmountInput.formatMoney(budget.amount.valueInCents, currencySymbol(homeCurrencyCode.code))
                } ?: run {
                    monthlyBudgetLabel = "Off"
                }
            }
            is Result.Error -> monthlyBudgetLabel = "Off"
        }
        // Load default category
        when (val result = defaultCategoryRepository.getDefaultCategoryId()) {
            is Result.Success -> result.data?.let { defaultCategoryId = it }
            is Result.Error -> Unit
        }
        // Load theme
        when (val result = themeRepository.getThemeMode()) {
            is Result.Success -> themeMode = result.data
            is Result.Error -> Unit
        }
        // Load language
        when (val result = localeRepository.getLanguageTag()) {
            is Result.Success -> if (result.data.isNotBlank()) languageTag = result.data
            is Result.Error -> Unit
        }
        // Load app version
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersion = packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            appVersion = "1.0.0"
        }
    }

    val hubState = remember(
        selectedCurrency, displayName, pinEnabled, monthlyBudgetLabel, appVersion,
        biometricEnrolled, biometricCapable, defaultCategoryId, themeMode, languageTag, profileNameFallback,
        themeLightLabel, themeDarkLabel, themeSystemLabel,
    ) {
        // No fictional-persona fallback (US-ONB-3: no name set -> generic, not "Maya").
        val profileInitial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        previewMoreHub.copy(
            profile = previewMoreHub.profile.copy(
                initial = profileInitial,
                name = displayName.ifBlank { profileNameFallback },
            ),
            settings = previewMoreHub.settings.map { setting ->
                when (setting.id) {
                    "currency" -> setting.copy(value = selectedCurrency)
                    "pin" -> setting.copy(value = if (pinEnabled) "On" else "Off")
                    "biometric" -> setting.copy(
                        toggleOn = biometricEnrolled,
                        enabled = pinEnabled && biometricCapable,
                    )
                    "budget" -> setting.copy(value = monthlyBudgetLabel)
                    "category" -> setting.copy(value = expenseCategoryLabel(defaultCategoryId))
                    "theme" -> setting.copy(
                        value = themeModeLabel(themeMode, themeLightLabel, themeDarkLabel, themeSystemLabel),
                    )
                    "language" -> setting.copy(value = AppLanguage.fromTag(languageTag).displayName)
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
                            "import" -> step = MoreStep.Import
                            "clear" -> step = MoreStep.Clear
                            "pin" -> if (pinEnabled) showPinManageSheet = true else onPinClick()
                            "budget" -> step = MoreStep.Budget
                            "category" -> step = MoreStep.DefaultCategory
                            "theme" -> step = MoreStep.Theme
                            "language" -> step = MoreStep.Language
                            "biometric" -> if (!pinEnabled) {
                                toastMessage = context.getString(R.string.more_biometric_requires_pin)
                            }
                        }
                    },
                    onSettingToggle = { id, on ->
                        if (id == "biometric" && pinEnabled && biometricCapable) {
                            scope.launch {
                                if (on) pinAuthRepository.enrollBiometric() else pinAuthRepository.clearBiometric()
                                biometricEnrolled = on
                            }
                        }
                    },
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                )
                MoreStep.Currency -> features.currency.SettingsFlow(
                    selectedCode = selectedCurrency,
                    onSelect = { newCode ->
                        selectedCurrency = newCode
                        scope.launch {
                            saveHomeCurrency(newCode)
                            val newCurrency = CurrencyCode(newCode)
                            homeCurrencyCode = newCurrency
                            onCurrencyChanged(newCurrency)
                        }
                    },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Export -> features.importExport.ExportFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Import -> features.importExport.ImportFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Clear -> features.importExport.ClearDataFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Reports -> features.reports.ReportsFlow(
                    onBack = { step = MoreStep.Hub },
                    onLogFirstExpense = {
                        step = MoreStep.Hub
                        onAddClick()
                    },
                )
                MoreStep.Categories -> features.categories.CategoryListFlow(
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.DefaultCategory -> MoreDefaultCategoryScreen(
                    selectedCategoryId = defaultCategoryId,
                    onSelect = { categoryId ->
                        defaultCategoryId = categoryId
                        onDefaultCategoryChanged(categoryId)
                        scope.launch {
                            defaultCategoryRepository.setDefaultCategoryId(categoryId)
                        }
                    },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Theme -> MoreThemeScreen(
                    selectedMode = themeMode,
                    onSelect = { mode ->
                        themeMode = mode
                        onThemeModeChanged(mode)
                        scope.launch { themeRepository.setThemeMode(mode) }
                    },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Language -> MoreLanguageScreen(
                    selectedLanguage = AppLanguage.fromTag(languageTag),
                    onSelect = { language ->
                        languageTag = language.tag
                        // Persist first (synchronous SharedPrefs write inside), then recreate —
                        // attachBaseContext() re-reads the fresh value on the new Activity instance.
                        scope.launch {
                            localeRepository.setLanguageTag(language.tag)
                            onLanguageChanged()
                        }
                    },
                    onBack = { step = MoreStep.Hub },
                )
                MoreStep.Budget -> MoreBudgetScreen(
                    currentAmount = monthlyBudgetLabel.takeIf { it != "Off" },
                    homeCurrency = homeCurrencyCode,
                    onSave = { money ->
                        scope.launch {
                            budgetRepository.setMonthlyBudget(money)
                            monthlyBudgetLabel = if (money != null) {
                                AmountInput.formatMoney(money.amount.valueInCents, currencySymbol(homeCurrencyCode.code))
                            } else {
                                "Off"
                            }
                            onBudgetChanged(money)
                            step = MoreStep.Hub
                        }
                    },
                    onBack = { step = MoreStep.Hub },
                )
            }
        }

        ProBottomSheetHost(
            visible = showPinManageSheet,
            title = null,
            onClose = { showPinManageSheet = false },
        ) {
            PinManageSheetContent(
                onChange = {
                    showPinManageSheet = false
                    showPinChangeFlow = true
                },
                onDisable = {
                    showPinManageSheet = false
                    showDisablePinConfirm = true
                },
                onCancel = { showPinManageSheet = false },
            )
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
                // Intent alone isn't authorization — the current PIN must be re-verified before
                // anything is actually turned off (US-AUTH-7).
                showDisablePinConfirm = false
                showDisablePinVerify = true
            },
            dismissLabel = stringResource(AuthR.string.pin_disable_confirm_cancel),
            onDismiss = { showDisablePinConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )

        if (showDisablePinVerify) {
            features.auth.PinVerifyFlow(
                headingRes = AuthR.string.pin_disable_verify_heading,
                helperRes = AuthR.string.pin_disable_verify_helper,
                onVerified = {
                    scope.launch {
                        disablePin()
                        pinEnabled = false
                        showDisablePinVerify = false
                    }
                },
                onCancel = { showDisablePinVerify = false },
                modifier = Modifier,
            )
        }

        if (showPinChangeFlow) {
            features.auth.PinChangeFlow(
                onDone = {
                    showPinChangeFlow = false
                    toastMessage = context.getString(AuthR.string.pin_change_success)
                },
                onCancel = { showPinChangeFlow = false },
                modifier = Modifier,
            )
        }

        ProToastHost(
            message = toastMessage,
            onDismiss = { toastMessage = null },
        )
    }
}

/** Change vs. disable choice for an already-enabled PIN (US-AUTH-7). */
@Composable
private fun PinManageSheetContent(
    onChange: () -> Unit,
    onDisable: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(dimens.space12),
    ) {
        ProButton(
            text = stringResource(AuthR.string.pin_manage_sheet_change),
            onClick = onChange,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
        )
        ProButton(
            text = stringResource(AuthR.string.pin_manage_sheet_disable),
            onClick = onDisable,
            variant = ProButtonVariant.Danger,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
        )
        ProButton(
            text = stringResource(AuthR.string.pin_manage_sheet_cancel),
            onClick = onCancel,
            variant = ProButtonVariant.Secondary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
        )
    }
}

private fun themeModeLabel(mode: ThemeMode, lightLabel: String, darkLabel: String, systemLabel: String): String =
    when (mode) {
        ThemeMode.LIGHT -> lightLabel
        ThemeMode.DARK -> darkLabel
        ThemeMode.SYSTEM -> systemLabel
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
