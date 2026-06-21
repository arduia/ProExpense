package com.arduia.expense.feature.debt.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.debt.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.debt.ui.preview.DEBT_PERSON_MAX
import com.arduia.expense.feature.debt.ui.preview.DebtAddFormState
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtAddLent
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DebtAddSheetContent(
    form: DebtAddFormState,
    onSideSelected: (DebtSide) -> Unit,
    onPersonChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onPickDate: () -> Unit,
    onPickDue: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        DebtSideToggle(side = form.side, onSideSelected = onSideSelected)

        DebtFieldGroup(label = stringResource(R.string.debt_person_label)) {
            DebtPersonField(
                value = form.person,
                counter = stringResource(
                    R.string.debt_person_counter,
                    form.person.length,
                    DEBT_PERSON_MAX,
                ),
                onValueChange = { onPersonChange(it.take(DEBT_PERSON_MAX)) },
            )
        }

        DebtFieldGroup(label = stringResource(R.string.debt_amount_label)) {
            DebtAmountField(rawValue = form.amountRaw, onValueChange = onAmountChange)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                DebtFieldLabel(stringResource(R.string.debt_date_label))
                DebtDatePill(
                    label = form.dateLabel,
                    isPlaceholder = false,
                    onClick = onPickDate,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                DebtFieldLabel(stringResource(R.string.debt_due_optional))
                DebtDatePill(
                    label = form.dueLabel ?: stringResource(R.string.debt_due_none),
                    isPlaceholder = form.dueLabel == null,
                    onClick = onPickDue,
                )
            }
        }

        ProButton(
            text = stringResource(R.string.debt_save_record),
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
private fun DebtSideToggle(
    side: DebtSide,
    onSideSelected: (DebtSide) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val containerShape = ProExpenseTheme.shapes.chip

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(colors.paperAlt)
            .padding(dimens.space4),
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        DebtToggleSegment(
            label = stringResource(R.string.debt_tab_lent),
            selected = side == DebtSide.Lent,
            accent = colors.success,
            onClick = { onSideSelected(DebtSide.Lent) },
            modifier = Modifier.weight(1f),
        )
        DebtToggleSegment(
            label = stringResource(R.string.debt_tab_owe),
            selected = side == DebtSide.Owe,
            accent = colors.danger,
            onClick = { onSideSelected(DebtSide.Owe) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DebtToggleSegment(
    label: String,
    selected: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val containerShape = ProExpenseTheme.shapes.chip

    Text(
        text = label,
        style = if (selected) typography.bodySemiBold else typography.bodyMedium,
        color = if (selected) accent else colors.onSurfaceMuted,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = modifier
            .defaultMinSize(minHeight = dimens.touchTargetMin)
            .minimumInteractiveComponentSize()
            .clip(containerShape)
            .background(if (selected) colors.surface else colors.paperAlt)
            .then(
                if (selected) {
                    Modifier.border(BorderStroke(1.dp, colors.line), containerShape)
                } else {
                    Modifier
                },
            )
            .proClickable(onClick = onClick, shape = containerShape)
            .padding(vertical = dimens.space8),
    )
}

@Composable
private fun DebtFieldGroup(
    label: String,
    content: @Composable () -> Unit,
) {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        DebtFieldLabel(label)
        content()
    }
}

@Composable
private fun DebtFieldLabel(label: String) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
}

@Composable
private fun DebtPersonField(
    value: String,
    counter: String,
    onValueChange: (String) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, colors.lineStrong), shape)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.debt_person_placeholder),
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
private fun DebtAmountField(
    rawValue: String,
    onValueChange: (String) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField

    BasicTextField(
        value = rawValue,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(9)) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, colors.lineStrong), shape)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.detailsAmount.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DebtCurrencyTransformation,
        singleLine = true,
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (rawValue.isEmpty()) {
                        Text(text = "$0", style = typography.detailsAmount, color = colors.muted)
                    }
                    inner()
                }
                Text(
                    text = stringResource(R.string.debt_currency_usd),
                    style = typography.caption,
                    color = colors.muted,
                )
            }
        },
    )
}

@Composable
private fun DebtDatePill(
    label: String,
    isPlaceholder: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            tint = colors.muted,
            size = dimens.iconInline,
        )
        Text(
            text = label,
            style = typography.bodyMedium,
            color = if (isPlaceholder) colors.muted else colors.onSurface,
        )
    }
}

/** Renders raw amount digits as a grouped "$" figure; cursor pinned to the field end. */
private object DebtCurrencyTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val grouped = if (digits.isEmpty()) "" else AmountInput.formatDisplay(digits)
        val display = "$$grouped"
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = if (offset <= 0) 1 else display.length
            override fun transformedToOriginal(offset: Int): Int = if (offset <= 1) 0 else digits.length
        }
        return TransformedText(AnnotatedString(display), mapping)
    }
}

@Preview(
    name = "Debt add — new record",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtAddSheetPreview() {
    ProExpenseTheme {
        Box(Modifier.fillMaxSize()) {
            DebtListScreen(
                state = com.arduia.expense.feature.debt.ui.preview.previewDebtLent,
                onSideSelected = {},
                onAddRecord = {},
                onRecordClick = {},
                onBack = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.debt_new_record),
                onClose = {},
            ) {
                DebtAddSheetContent(
                    form = previewDebtAddLent,
                    onSideSelected = {},
                    onPersonChange = {},
                    onAmountChange = {},
                    onPickDate = {},
                    onPickDue = {},
                    onSave = {},
                )
            }
        }
    }
}
