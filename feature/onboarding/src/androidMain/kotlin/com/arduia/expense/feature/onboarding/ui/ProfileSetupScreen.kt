package com.arduia.expense.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.ui.design.CurrencyOption
import com.arduia.expense.ui.design.CurrencyPickerContent
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProfileCurrencyRow
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.ProfileStepHeader
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.design.defaultCurrencyOptions
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class ProfileSetupStep {
    Name,
    Currency,
}

data class ProfileSetupState(
    val step: ProfileSetupStep = ProfileSetupStep.Name,
    val name: String = "",
    val selectedCurrencyCode: String = "USD",
    val showCurrencySheet: Boolean = false,
    val currencySearchQuery: String = "",
)

@Composable
fun ProfileSetupScreenContent(
    state: ProfileSetupState,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit,
    onStartTracking: () -> Unit,
    onSkip: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    onOpenCurrencySheet: () -> Unit,
    onCloseCurrencySheet: () -> Unit,
    onCurrencySearchChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencyOptions: List<CurrencyOption> = defaultCurrencyOptions,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BoxWithSheet(
        showSheet = state.showCurrencySheet,
        sheetTitle = stringResource(R.string.currency_picker_title),
        onCloseSheet = onCloseCurrencySheet,
        sheetContent = {
            SearchField(
                value = state.currencySearchQuery,
                onValueChange = onCurrencySearchChange,
                placeholder = stringResource(R.string.search_currency_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space12),
            )
            val filtered = currencyOptions.filter { option ->
                state.currencySearchQuery.isBlank() ||
                    option.code.contains(state.currencySearchQuery, ignoreCase = true) ||
                    option.label.contains(state.currencySearchQuery, ignoreCase = true)
            }
            CurrencyPickerContent(
                options = filtered,
                selectedCode = state.selectedCurrencyCode,
                onSelected = { code ->
                    onCurrencySelected(code)
                    onCloseCurrencySheet()
                },
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space24),
        ) {
            when (state.step) {
                ProfileSetupStep.Name -> {
                    ProfileStepHeader(
                        step = 1,
                        totalSteps = 2,
                        eyebrow = stringResource(R.string.profile_setup_step, 1, 2),
                        title = stringResource(R.string.profile_setup_title),
                        subtitle = stringResource(R.string.profile_setup_description),
                        onSkip = onSkip,
                        skipLabel = stringResource(R.string.skip),
                        modifier = Modifier.padding(top = dimens.space8),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                        Text(
                            text = stringResource(R.string.profile_name_label),
                            style = typography.eyebrow,
                            color = colors.primary,
                        )
                        ProfileNameField(
                            value = state.name,
                            onValueChange = onNameChange,
                            placeholder = stringResource(R.string.profile_name_hint),
                        )
                        Text(
                            text = stringResource(R.string.profile_name_helper),
                            style = typography.caption,
                            color = colors.muted,
                        )
                    }
                    ProButton(
                        text = stringResource(R.string.continue_label),
                        onClick = onContinue,
                        size = ProButtonSize.Lg,
                        fillMaxWidth = true,
                        modifier = Modifier.padding(top = dimens.space24),
                    )
                }
                ProfileSetupStep.Currency -> {
                    ProfileStepHeader(
                        step = 2,
                        totalSteps = 2,
                        eyebrow = stringResource(R.string.profile_setup_step, 2, 2),
                        title = stringResource(R.string.profile_currency_title),
                        subtitle = stringResource(R.string.profile_currency_description),
                        onSkip = onSkip,
                        skipLabel = stringResource(R.string.skip),
                        modifier = Modifier.padding(top = dimens.space8),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                        currencyOptions.forEach { option ->
                            ProfileCurrencyRow(
                                code = option.code,
                                label = option.label,
                                selected = option.code == state.selectedCurrencyCode,
                                onClick = {
                                    if (option.code == state.selectedCurrencyCode) {
                                        onOpenCurrencySheet()
                                    } else {
                                        onCurrencySelected(option.code)
                                    }
                                },
                            )
                        }
                        ProTextAction(
                            text = stringResource(R.string.currency_picker_title),
                            onClick = onOpenCurrencySheet,
                            style = typography.bodySemiBold,
                            color = colors.primary,
                            modifier = Modifier.padding(top = dimens.space4),
                        )
                    }
                    ProButton(
                        text = stringResource(R.string.start_tracking),
                        onClick = onStartTracking,
                        size = ProButtonSize.Lg,
                        fillMaxWidth = true,
                        modifier = Modifier.padding(top = dimens.space24),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxWithSheet(
    showSheet: Boolean,
    sheetTitle: String,
    onCloseSheet: () -> Unit,
    sheetContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        ProBottomSheetHost(
            visible = showSheet,
            title = sheetTitle,
            onClose = onCloseSheet,
            sheetContent = sheetContent,
        )
    }
}

@Preview(
    name = "Profile setup — name",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileSetupNamePreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(name = "Maya"),
            onNameChange = {},
            onContinue = {},
            onStartTracking = {},
            onSkip = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}

@Preview(
    name = "Profile setup — currency",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileSetupCurrencyPreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(step = ProfileSetupStep.Currency),
            onNameChange = {},
            onContinue = {},
            onStartTracking = {},
            onSkip = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}

@Preview(
    name = "Profile setup — currency sheet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileSetupCurrencySheetPreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(
                step = ProfileSetupStep.Currency,
                showCurrencySheet = true,
            ),
            onNameChange = {},
            onContinue = {},
            onStartTracking = {},
            onSkip = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}
