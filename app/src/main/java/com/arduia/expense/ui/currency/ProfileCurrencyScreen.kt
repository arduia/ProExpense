package com.arduia.expense.ui.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.CurrencyPickerContent
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.defaultCurrencyOptions
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.onboarding.SetupStepIndicator
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private const val PROFILE_SETUP_STEPS = 2

@Composable
fun ProfileCurrencyScreenContent(
    selectedCode: String,
    showPicker: Boolean,
    onOpenPicker: () -> Unit,
    onClosePicker: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val selectedLabel = defaultCurrencyOptions.firstOrNull { it.code == selectedCode }?.label.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.space18),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimens.space8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            SetupStepIndicator(currentStep = 2, totalSteps = PROFILE_SETUP_STEPS)
            Text(
                text = stringResource(R.string.skip),
                style = typography.bodyMedium,
                color = colors.muted,
                modifier = Modifier
                    .proClickable(onClick = onSkip, shape = ProExpenseTheme.shapes.chip)
                    .padding(dimens.space8),
            )
        }

        Spacer(modifier = Modifier.height(dimens.space24))

        Text(
            text = stringResource(R.string.profile_setup_step, 2, PROFILE_SETUP_STEPS),
            style = typography.eyebrow,
            color = colors.primary,
        )
        Text(
            text = stringResource(R.string.profile_currency_title),
            style = typography.screenTitle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space12),
        )
        Text(
            text = stringResource(R.string.profile_currency_description),
            style = typography.body,
            color = colors.onSurfaceMuted,
            modifier = Modifier.padding(top = dimens.space16),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space32)
                .proClickable(onClick = onOpenPicker, shape = ProExpenseTheme.shapes.searchField)
                .background(colors.surface, ProExpenseTheme.shapes.searchField)
                .padding(dimens.space16),
        ) {
            Text(text = selectedCode, style = typography.sectionHead, color = colors.onSurface)
            Text(text = selectedLabel, style = typography.caption, color = colors.onSurfaceMuted)
        }

        Spacer(modifier = Modifier.weight(1f))

        ProButton(
            text = stringResource(R.string.continue_label),
            onClick = onContinue,
            size = ProButtonSize.Lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.space24),
        )
    }

    if (showPicker) {
        ProBottomSheetHost(
            title = stringResource(R.string.currency_picker_title),
            onClose = onClosePicker,
        ) {
            CurrencyPickerContent(
                options = defaultCurrencyOptions,
                selectedCode = selectedCode,
                onSelected = onCurrencySelected,
            )
        }
    }
}

@Composable
fun CurrencySettingScreenContent(
    selectedCode: String,
    showPicker: Boolean,
    onOpenPicker: () -> Unit,
    onClosePicker: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val selectedLabel = defaultCurrencyOptions.firstOrNull { it.code == selectedCode }?.label.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.currency_setting_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space16)
                .proClickable(onClick = onOpenPicker, shape = ProExpenseTheme.shapes.searchField)
                .background(colors.surface, ProExpenseTheme.shapes.searchField)
                .padding(dimens.space16),
        ) {
            Text(text = selectedCode, style = typography.sectionHead, color = colors.onSurface)
            Text(text = selectedLabel, style = typography.caption, color = colors.onSurfaceMuted)
        }
    }

    if (showPicker) {
        ProBottomSheetHost(
            title = stringResource(R.string.currency_picker_title),
            onClose = onClosePicker,
        ) {
            CurrencyPickerContent(
                options = defaultCurrencyOptions,
                selectedCode = selectedCode,
                onSelected = onCurrencySelected,
            )
        }
    }
}

@Preview(
    name = "Profile currency",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileCurrencyPreview() {
    ProExpenseTheme {
        ProfileCurrencyScreenContent(
            selectedCode = "USD",
            showPicker = false,
            onOpenPicker = {},
            onClosePicker = {},
            onCurrencySelected = {},
            onContinue = {},
            onSkip = {},
        )
    }
}

@Preview(
    name = "Profile currency — picker",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileCurrencyPickerPreview() {
    ProExpenseTheme {
        ProfileCurrencyScreenContent(
            selectedCode = "USD",
            showPicker = true,
            onOpenPicker = {},
            onClosePicker = {},
            onCurrencySelected = {},
            onContinue = {},
            onSkip = {},
        )
    }
}

@Preview(
    name = "Currency setting",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CurrencySettingPreview() {
    ProExpenseTheme {
        CurrencySettingScreenContent(
            selectedCode = "USD",
            showPicker = false,
            onOpenPicker = {},
            onClosePicker = {},
            onCurrencySelected = {},
            onBack = {},
        )
    }
}
