package com.arduia.expense.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.EVENT_NAME_MAX
import com.arduia.expense.ui.preview.EventCreateFormState
import com.arduia.expense.ui.preview.previewEventCreateErrors
import com.arduia.expense.ui.preview.previewEventCreateValid
import com.arduia.expense.ui.preview.previewEventList
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun EventCreateSheetContent(
    form: EventCreateFormState,
    onNameChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        EventFieldGroup(label = stringResource(R.string.event_name_label)) {
            EventNameField(
                value = form.name,
                counter = stringResource(R.string.event_name_counter, form.name.length, EVENT_NAME_MAX),
                isError = form.isDuplicateName,
                onValueChange = { onNameChange(it.take(EVENT_NAME_MAX)) },
            )
            if (form.isDuplicateName) {
                EventFieldError(message = stringResource(R.string.event_name_duplicate))
            }
        }

        EventFieldGroup(label = stringResource(R.string.event_dates_label)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space12),
            ) {
                EventDatePill(label = form.startLabel, onClick = onPickStart)
                ProIcon(
                    glyph = ProIconGlyph.ChevronRight,
                    contentDescription = null,
                    tint = ProExpenseTheme.colors.muted,
                    size = dimens.iconInline,
                )
                EventDatePill(label = form.endLabel, onClick = onPickEnd)
            }
        }

        EventFieldGroup(label = stringResource(R.string.event_total_budget_label)) {
            EventBudgetField(
                rawValue = form.budgetRaw,
                isError = form.showBudgetError && !AmountInput.canProceed(form.budgetRaw),
                onValueChange = onBudgetChange,
            )
            if (form.showBudgetError && !AmountInput.canProceed(form.budgetRaw)) {
                EventFieldError(message = stringResource(R.string.event_budget_positive_error))
            }
        }

        ProButton(
            text = stringResource(R.string.event_create_cta),
            onClick = onSave,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            enabled = form.canSave,
            modifier = Modifier.padding(top = dimens.space4),
        )
    }
}

@Composable
private fun EventFieldGroup(
    label: String,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        content()
    }
}

@Composable
private fun EventFieldError(message: String) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Close,
            contentDescription = null,
            tint = colors.danger,
            size = dimens.iconClear,
        )
        Text(text = message, style = typography.caption, color = colors.danger)
    }
}

@Composable
private fun EventNameField(
    value: String,
    counter: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField
    val borderColor = if (isError) colors.danger else colors.lineStrong

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, borderColor), shape)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        maxLines = 2,
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.event_name_placeholder),
                            style = typography.body,
                            color = colors.muted,
                        )
                    }
                    inner()
                }
                Text(
                    text = counter,
                    style = typography.caption,
                    color = colors.muted,
                    modifier = Modifier.padding(start = dimens.space8),
                )
            }
        },
    )
}

@Composable
private fun EventDatePill(
    label: String,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField

    Row(
        modifier = Modifier
            .clip(shape)
            .border(BorderStroke(1.dp, colors.lineStrong), shape)
            .background(colors.surface)
            .proClickable(onClick = onClick, shape = shape)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Calendar,
            contentDescription = null,
            tint = colors.onSurfaceMuted,
            size = dimens.iconInline,
        )
        Text(text = label, style = typography.bodyMedium, color = colors.onSurface)
    }
}

@Composable
private fun EventBudgetField(
    rawValue: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField
    val borderColor = if (isError) colors.danger else colors.lineStrong

    BasicTextField(
        value = rawValue,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(9)) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, borderColor), shape)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.detailsAmount.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = CurrencyVisualTransformation,
        singleLine = true,
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (rawValue.isEmpty()) {
                        Text(
                            text = "$0",
                            style = typography.detailsAmount,
                            color = colors.muted,
                        )
                    }
                    inner()
                }
                Text(
                    text = stringResource(R.string.event_currency_usd),
                    style = typography.caption,
                    color = colors.muted,
                )
            }
        },
    )
}

/** Renders raw budget digits as a grouped "$" amount; cursor is kept at the field end. */
private object CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val grouped = if (digits.isEmpty()) "" else AmountInput.formatDisplay(digits)
        val display = "$$grouped"
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 0) 1 else display.length

            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 1) 0 else digits.length
        }
        return TransformedText(AnnotatedString(display), mapping)
    }
}

@Preview(
    name = "Event create — valid",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventCreateValidPreview() {
    ProExpenseTheme {
        Box(Modifier.fillMaxSize()) {
            EventBudgetListScreen(
                events = previewEventList,
                onCreateEvent = {},
                onEventClick = {},
                selectedTab = HomeNavTab.Budget,
                onTabSelected = {},
                onAddClick = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.event_create_title),
                onClose = {},
            ) {
                EventCreateSheetContent(
                    form = previewEventCreateValid,
                    onNameChange = {},
                    onBudgetChange = {},
                    onPickStart = {},
                    onPickEnd = {},
                    onSave = {},
                )
            }
        }
    }
}

@Preview(
    name = "Event create — errors",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventCreateErrorsPreview() {
    ProExpenseTheme {
        Box(Modifier.fillMaxSize()) {
            EventBudgetListScreen(
                events = previewEventList,
                onCreateEvent = {},
                onEventClick = {},
                selectedTab = HomeNavTab.Budget,
                onTabSelected = {},
                onAddClick = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.event_create_title),
                onClose = {},
            ) {
                EventCreateSheetContent(
                    form = previewEventCreateErrors,
                    onNameChange = {},
                    onBudgetChange = {},
                    onPickStart = {},
                    onPickEnd = {},
                    onSave = {},
                )
            }
        }
    }
}
