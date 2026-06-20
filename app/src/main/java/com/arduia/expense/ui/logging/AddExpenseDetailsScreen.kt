package com.arduia.expense.ui.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.DetailAmountSummaryCard
import com.arduia.expense.ui.design.DetailDateTimeField
import com.arduia.expense.ui.design.DetailNoteField
import com.arduia.expense.ui.design.DetailTagField
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.TagLinkOption
import com.arduia.expense.ui.design.TagPickerContent
import com.arduia.expense.ui.design.customExpenseCategories
import com.arduia.expense.ui.design.defaultExpenseCategories
import com.arduia.expense.ui.preview.ExpenseEntryState
import com.arduia.expense.ui.preview.previewExpenseDetails
import com.arduia.expense.ui.preview.previewExpenseDetailsNoteLimit
import com.arduia.expense.ui.preview.previewTagDebts
import com.arduia.expense.ui.preview.previewTagEvents
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private const val NOTE_MAX_LENGTH = 200

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
    tagEvents: List<TagLinkOption> = previewTagEvents,
    tagDebts: List<TagLinkOption> = previewTagDebts,
    showTagField: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val displayAmount = AmountInput.formatDisplay(state.rawAmount)
    val formattedSaveAmount = "$${displayAmount}"
    val atNoteLimit = state.note.length >= NOTE_MAX_LENGTH

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            ProTopBar(
                title = stringResource(R.string.details),
                onBack = onBackToAmount,
                backLabel = stringResource(R.string.amount_step),
            )

            DetailAmountSummaryCard(
                amountLabel = formattedSaveAmount,
                onEdit = onBackToAmount,
            )

            CategoryPicker(
                defaultCategories = defaultExpenseCategories,
                customCategories = customExpenseCategories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = onCategorySelected,
                showCustomSection = true,
                showAddChip = true,
                onAddClick = {},
            )

            DetailDateTimeField(
                dateLabel = state.dateLabel,
                timeLabel = state.timeLabel,
                onClick = onDateClick,
            )

            DetailNoteField(
                value = state.note,
                onValueChange = onNoteChange,
                maxLength = NOTE_MAX_LENGTH,
                placeholder = stringResource(R.string.note),
                atLimit = atNoteLimit,
                errorMessage = if (atNoteLimit) {
                    stringResource(R.string.note_max_length_error)
                } else {
                    null
                },
            )

            if (showTagField) {
                DetailTagField(
                    tagLabel = state.linkedTagLabel?.let { label ->
                        stringResource(R.string.event_tag_format, label)
                    },
                    placeholder = stringResource(R.string.add_event_tag),
                    onClick = onOpenTagSheet,
                    onClear = onClearTag,
                )
            }

            ProButton(
                text = stringResource(R.string.save_expense, formattedSaveAmount),
                onClick = onSave,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                modifier = Modifier.padding(top = dimens.space8),
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
