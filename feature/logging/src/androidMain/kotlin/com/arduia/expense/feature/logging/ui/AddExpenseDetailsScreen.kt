package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.logging.R
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.logging.ui.preview.hasValidExchangeRate
import com.arduia.expense.feature.logging.ui.preview.isForeignCurrency
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetails
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsForeignCurrency
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsForeignCurrencyNoRate
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsNoteLimit
import com.arduia.expense.feature.logging.ui.preview.previewIncomeAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewTagDebts
import com.arduia.expense.feature.logging.ui.preview.previewTagEvents
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.DetailAmountSummaryCard
import com.arduia.expense.ui.design.DetailDateTimeField
import com.arduia.expense.ui.design.DetailFieldCard
import com.arduia.expense.ui.design.DetailNoteField
import com.arduia.expense.ui.design.DetailTagField
import com.arduia.expense.ui.design.NOTE_INPUT_MAX_LENGTH
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.TagLinkOption
import com.arduia.expense.ui.design.TagPickerContent
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.customExpenseCategories
import com.arduia.expense.ui.design.defaultExpenseCategories
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun AddExpenseDetailsScreen(
    state: ExpenseEntryState,
    onBackToAmount: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onOpenTagSheet: () -> Unit,
    onCloseTagSheet: () -> Unit,
    onTagSelected: (TagLinkOption) -> Unit,
    onClearTag: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    onExchangeRateChange: (String) -> Unit = {},
    onAddCategory: () -> Unit = {},
    tagEvents: List<TagLinkOption> = previewTagEvents,
    tagDebts: List<TagLinkOption> = previewTagDebts,
    showTagField: Boolean = true,
    defaultCategories: List<Pair<String, String>> = defaultExpenseCategories,
    customCategories: List<Pair<String, String>> = customExpenseCategories,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val displayAmount = AmountInput.formatDisplay(state.rawAmount)
    val formattedSaveAmount = currencySymbol(state.currencyCode) + displayAmount
    val atNoteLimit = state.note.length >= NOTE_INPUT_MAX_LENGTH
    val rate = state.exchangeRateRaw.trim().toDoubleOrNull()
    val convertedLabel =
        if (state.isForeignCurrency() && rate != null && rate > 0.0) {
            val rawAmount = state.rawAmount.toDoubleOrNull() ?: 0.0
            val convertedCents = Math.round(rawAmount * rate * 100)
            AmountInput.formatMoney(convertedCents, currencySymbol(state.homeCurrencyCode))
        } else {
            null
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            ProFlatHeader(
                title = stringResource(R.string.details),
                onBack = onBackToAmount,
                backContentDescription = stringResource(R.string.amount_step),
                modifier = Modifier.padding(vertical = dimens.space14),
            )

            DetailAmountSummaryCard(
                amountLabel = formattedSaveAmount,
                onEdit = onBackToAmount,
            )

            if (state.isForeignCurrency()) {
                ExchangeRateField(
                    fromCode = state.currencyCode,
                    toCode = state.homeCurrencyCode,
                    rateRaw = state.exchangeRateRaw,
                    onRateChange = onExchangeRateChange,
                    convertedLabel = convertedLabel,
                )
            }

            CategoryPicker(
                defaultCategories = defaultCategories,
                customCategories = customCategories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = onCategorySelected,
                showCustomSection = true,
                showAddChip = true,
                onAddClick = onAddCategory,
            )

            DetailDateTimeField(
                dateLabel = state.dateLabel,
                timeLabel = state.timeLabel,
                onClick = onDateClick,
            )

            DetailNoteField(
                value = state.note,
                onValueChange = onNoteChange,
                maxLength = NOTE_INPUT_MAX_LENGTH,
                placeholder = stringResource(R.string.note),
                atLimit = atNoteLimit,
                errorMessage =
                    if (atNoteLimit) {
                        stringResource(R.string.note_max_length_error)
                    } else {
                        null
                    },
            )

            if (showTagField) {
                DetailTagField(
                    // The field's own leading icon already renders "@" — no need to repeat it here.
                    tagLabel = state.linkedTagLabel,
                    placeholder = stringResource(R.string.add_event_tag),
                    onClick = onOpenTagSheet,
                    onClear = onClearTag,
                )
            }

            ProButton(
                text = stringResource(R.string.save_expense, formattedSaveAmount),
                onClick = onSave,
                enabled = state.selectedCategoryId.isNotBlank() && state.hasValidExchangeRate(),
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }

        // Overlay the tag picker on top of the content; it must not take layout space when hidden.
        ProBottomSheetHost(
            visible = state.showTagSheet,
            title = stringResource(R.string.link_to_title),
            onClose = onCloseTagSheet,
            sheetContent = {
                TagPickerContent(
                    events = tagEvents,
                    debts = tagDebts,
                    selectedId = state.linkedTagId,
                    selectedKind = state.linkedTagKind,
                    onSelected = { option ->
                        onTagSelected(option)
                        onCloseTagSheet()
                    },
                )
            },
        )
    }
}

@Composable
private fun ExchangeRateField(
    fromCode: String,
    toCode: String,
    rateRaw: String,
    onRateChange: (String) -> Unit,
    convertedLabel: String?,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        DetailFieldCard(
            leading = {
                ProIcon(
                    glyph = ProIconGlyph.Budget,
                    contentDescription = null,
                    tint = colors.muted,
                    size = dimens.iconNav,
                )
            },
        ) {
            Column {
                Text(
                    text = stringResource(R.string.exchange_rate_label, fromCode, toCode),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
                BasicTextField(
                    value = rateRaw,
                    onValueChange = onRateChange,
                    textStyle = typography.body.copy(color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 28.dp),
                    decorationBox = { inner ->
                        if (rateRaw.isEmpty()) {
                            Text(
                                text = stringResource(R.string.exchange_rate_placeholder),
                                style = typography.body,
                                color = colors.muted,
                            )
                        }
                        inner()
                    },
                )
            }
        }
        if (convertedLabel != null) {
            Text(
                text = stringResource(R.string.exchange_rate_converted, convertedLabel),
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space6),
            )
        }
    }
}

@Preview(
    name = "Add expense — details with tag",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseDetailsPreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewExpenseDetails,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add expense — details with tag (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseDetailsDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        AddExpenseDetailsScreen(
            state = previewExpenseDetails,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add expense — note at limit",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseDetailsNoteLimitPreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewExpenseDetailsNoteLimit,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add expense — tag sheet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseTagSheetPreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewExpenseDetails.copy(showTagSheet = true),
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add expense — foreign currency with rate",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseDetailsForeignCurrencyPreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewExpenseDetailsForeignCurrency,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add expense — foreign currency, no rate yet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseDetailsForeignCurrencyNoRatePreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewExpenseDetailsForeignCurrencyNoRate,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
        )
    }
}

@Preview(
    name = "Add income — details",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddIncomeDetailsPreview() {
    ProExpenseTheme {
        AddExpenseDetailsScreen(
            state = previewIncomeAmountTyped,
            onBackToAmount = {},
            onCategorySelected = {},
            onNoteChange = {},
            onDateClick = {},
            onOpenTagSheet = {},
            onCloseTagSheet = {},
            onTagSelected = {},
            onClearTag = {},
            onSave = {},
            defaultCategories = listOf("income" to "Income", "salary" to "Salary", "gift" to "Gift"),
        )
    }
}
