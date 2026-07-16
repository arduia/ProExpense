package com.arduia.expense.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.onboarding.R
import com.arduia.expense.ui.design.CurrencyOption
import com.arduia.expense.ui.design.CurrencyPickerContent
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProfileCurrencyRow
import com.arduia.expense.ui.design.ProfileIdentityCard
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.ProfileStepHeader
import com.arduia.expense.ui.design.SearchField
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.defaultCurrencyOptions
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private const val CURRENCY_QUICK_PICKS = 4

data class ProfileSetupState(
    val name: String = "",
    val selectedCurrencyCode: String = "USD",
    val showCurrencySheet: Boolean = false,
    val currencySearchQuery: String = "",
) {
    val canStartTracking: Boolean get() = name.trim().isNotEmpty()
}

@Composable
fun ProfileSetupScreenContent(
    state: ProfileSetupState,
    onNameChange: (String) -> Unit,
    onStartTracking: () -> Unit,
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

    val selectedOption = currencyOptions.firstOrNull { it.code == state.selectedCurrencyCode }
    val currencyLabel = selectedOption?.label ?: state.selectedCurrencyCode
    val trimmedName = state.name.trim()
    val greeting =
        if (trimmedName.isEmpty()) {
            stringResource(R.string.profile_identity_greeting_empty)
        } else {
            stringResource(R.string.profile_identity_greeting, trimmedName)
        }

    // The four most common currencies, but always surface the selected one so it can stay checked.
    val quickPicks =
        remember(currencyOptions, state.selectedCurrencyCode) {
            val base = currencyOptions.take(CURRENCY_QUICK_PICKS).toMutableList()
            if (base.none { it.code == state.selectedCurrencyCode }) {
                currencyOptions.firstOrNull { it.code == state.selectedCurrencyCode }?.let { picked ->
                    if (base.isNotEmpty()) base[base.lastIndex] = picked
                }
            }
            base.toList()
        }

    BoxWithSheet(
        showSheet = state.showCurrencySheet,
        sheetTitle = stringResource(R.string.currency_picker_title),
        onCloseSheet = onCloseCurrencySheet,
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SearchField(
                    value = state.currencySearchQuery,
                    onValueChange = onCurrencySearchChange,
                    placeholder = stringResource(R.string.search_currency_placeholder),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimens.space12),
                )
                val filtered =
                    currencyOptions.filter { option ->
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
            }
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(colors.paper)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimens.space20),
            ) {
                ProfileStepHeader(
                    step = 1,
                    totalSteps = 1,
                    eyebrow = stringResource(R.string.profile_setup_eyebrow),
                    title = stringResource(R.string.profile_setup_title),
                    subtitle = stringResource(R.string.profile_setup_description),
                    modifier = Modifier.padding(top = dimens.space8),
                )

                ProfileIdentityCard(
                    eyebrow = stringResource(R.string.profile_identity_eyebrow),
                    greeting = greeting,
                    trackingLabel =
                        stringResource(
                            R.string.profile_identity_tracking,
                            currencyLabel,
                            state.selectedCurrencyCode,
                        ),
                    currencySymbol = currencySymbol(state.selectedCurrencyCode),
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
                }

                Column(verticalArrangement = Arrangement.spacedBy(dimens.space12)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_currency_label),
                            style = typography.eyebrow,
                            color = colors.primary,
                        )
                        Text(
                            text = stringResource(R.string.profile_currency_hint),
                            style = typography.caption,
                            color = colors.onSurfaceMuted,
                        )
                    }
                    CurrencyQuickGrid(
                        options = quickPicks,
                        selectedCode = state.selectedCurrencyCode,
                        onSelected = onCurrencySelected,
                    )
                    MoreCurrenciesButton(
                        text = stringResource(R.string.profile_more_currencies),
                        onClick = onOpenCurrencySheet,
                    )
                }
            }

            ProButton(
                text = stringResource(R.string.start_tracking),
                onClick = onStartTracking,
                size = ProButtonSize.Lg,
                enabled = state.canStartTracking,
                fillMaxWidth = true,
                modifier = Modifier.padding(top = dimens.space16),
            )
        }
    }
}

@Composable
private fun CurrencyQuickGrid(
    options: List<CurrencyOption>,
    selectedCode: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space10),
    ) {
        options.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(dimens.space10),
            ) {
                rowItems.forEach { option ->
                    ProfileCurrencyRow(
                        code = option.code,
                        label = option.label,
                        selected = option.code == selectedCode,
                        onClick = { onSelected(option.code) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MoreCurrenciesButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField
    val strokeWidth = dimens.buttonBorderWidth
    val cornerRadius = dimens.tileRadius
    val dashOn = dimens.space8
    val dashOff = dimens.space6
    val borderColor = colors.lineStrong

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .drawBehind {
                    val sw = strokeWidth.toPx()
                    val r = cornerRadius.toPx()
                    drawRoundRect(
                        color = borderColor,
                        topLeft = Offset(sw / 2f, sw / 2f),
                        size = Size(size.width - sw, size.height - sw),
                        cornerRadius = CornerRadius(r, r),
                        style =
                            Stroke(
                                width = sw,
                                pathEffect =
                                    PathEffect.dashPathEffect(
                                        floatArrayOf(dashOn.toPx(), dashOff.toPx()),
                                        0f,
                                    ),
                            ),
                    )
                }.proClickable(onClick = onClick, shape = shape)
                .padding(horizontal = dimens.space14, vertical = dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8, Alignment.CenterHorizontally),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Search,
            contentDescription = null,
            tint = colors.primary,
            size = dimens.iconInline,
        )
        Text(text = text, style = typography.bodySemiBold, color = colors.primary)
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
    name = "Profile setup — merged",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileSetupMergedPreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(name = "Maya"),
            onNameChange = {},
            onStartTracking = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}

@Preview(
    name = "Profile setup — empty name",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileSetupEmptyPreview() {
    ProExpenseTheme {
        ProfileSetupScreenContent(
            state = ProfileSetupState(selectedCurrencyCode = "EUR"),
            onNameChange = {},
            onStartTracking = {},
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
            state = ProfileSetupState(showCurrencySheet = true),
            onNameChange = {},
            onStartTracking = {},
            onCurrencySelected = {},
            onOpenCurrencySheet = {},
            onCloseCurrencySheet = {},
            onCurrencySearchChange = {},
        )
    }
}
