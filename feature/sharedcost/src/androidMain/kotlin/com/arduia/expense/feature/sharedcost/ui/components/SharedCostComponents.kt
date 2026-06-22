package com.arduia.expense.feature.sharedcost.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.ui.design.DetailFieldCard
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.design.proPressScale
import com.arduia.expense.ui.design.proRippleClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

@Composable
fun SharedCostPeopleCard(
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 2,
    max: Int = 20,
    maxReachedHint: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    val canDecrement = count > min
    val canIncrement = count < max

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(BorderStroke(1.dp, colors.line), shape)
                .background(colors.surface)
                .padding(horizontal = dimens.space16, vertical = dimens.rowPaddingV),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SharedCostStepperButton(
                glyph = ProIconGlyph.Minus,
                enabled = canDecrement,
                onClick = onDecrement,
                filled = false,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = count.toString(),
                    style = typography.displayAmount,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.shared_people_word),
                    style = typography.eyebrow,
                    color = colors.muted,
                )
            }
            SharedCostStepperButton(
                glyph = ProIconGlyph.Plus,
                enabled = canIncrement,
                onClick = onIncrement,
                filled = true,
            )
        }
        if (maxReachedHint != null && !canIncrement) {
            Text(
                text = maxReachedHint,
                style = typography.caption,
                color = colors.muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SharedCostStepperButton(
    glyph: ProIconGlyph,
    enabled: Boolean,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val shape = if (filled) ProExpenseTheme.shapes.tile else CircleShape
    val alpha = if (enabled) 1f else motion.keypadDisabledOpacity
    val background = when {
        filled && enabled -> colors.primary
        filled -> colors.primary.copy(alpha = alpha)
        else -> colors.surface
    }
    val tint = when {
        filled && enabled -> colors.onPrimaryWarm
        filled -> colors.onPrimaryWarm.copy(alpha = alpha)
        enabled -> colors.onSurface
        else -> colors.muted2
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(dimens.touchTargetMin)
            .proPressScale(interactionSource, enabled = enabled)
            .clip(shape)
            .then(
                if (!filled) {
                    Modifier.border(BorderStroke(1.dp, colors.lineStrong), shape)
                } else {
                    Modifier
                },
            )
            .background(background)
            .then(
                if (filled) {
                    Modifier.proRippleClickable(
                        onClick = onClick,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        role = Role.Button,
                    )
                } else {
                    Modifier.proIconClickable(
                        onClick = onClick,
                        enabled = enabled,
                        interactionSource = interactionSource,
                    )
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = glyph,
            contentDescription = null,
            tint = tint,
            size = dimens.iconInline,
        )
    }
}

@Composable
fun SharedCostParticipantRow(
    index: Int,
    name: String,
    amount: String,
    modifier: Modifier = Modifier,
    showNoteIcon: Boolean = false,
    editableAmount: Boolean = false,
    onAmountChange: (String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.rowPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.space32)
                .clip(CircleShape)
                .background(colors.primarySoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = index.toString(),
                style = typography.bodySemiBold.centeredGlyph(),
                color = colors.primary,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            Text(
                text = name,
                style = typography.captionMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showNoteIcon) {
                ProIcon(
                    glyph = ProIconGlyph.Note,
                    contentDescription = null,
                    tint = colors.muted,
                    size = dimens.iconTag,
                )
            }
        }
        if (editableAmount) {
            BasicTextField(
                value = amount.removePrefix("$"),
                onValueChange = onAmountChange,
                textStyle = typography.listAmount.copy(
                    color = colors.onSurface,
                    fontFamily = typography.amountFamily,
                    textAlign = TextAlign.End,
                ),
                cursorBrush = SolidColor(colors.primary),
                singleLine = true,
                modifier = Modifier.padding(start = dimens.space8),
            )
        } else {
            Text(
                text = amount,
                style = typography.listAmount,
                color = colors.onSurface,
            )
        }
    }
}

@Composable
fun SharedCostHistoryRow(
    title: String,
    meta: String,
    total: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimens.touchTargetMin)
            .clip(shape)
            .border(BorderStroke(1.dp, colors.line), shape)
            .background(colors.surface)
            .proClickable(onClick = onClick, shape = shape)
            .padding(dimens.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.quickAccessIconSize)
                .clip(ProExpenseTheme.shapes.tile)
                .background(colors.primarySoft),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.FeatSplit,
                contentDescription = null,
                tint = colors.primary,
                size = dimens.iconInline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                style = typography.caption,
                color = colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        Text(
            text = total,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Composable
fun SharedCostNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    DetailFieldCard(
        modifier = modifier,
        leading = {
            ProIcon(
                glyph = ProIconGlyph.Note,
                contentDescription = null,
                tint = colors.muted,
                size = ProExpenseTheme.dimensions.iconNav,
            )
        },
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.body.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = typography.body.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        ),
                        color = colors.muted,
                    )
                }
                inner()
            },
        )
    }
}

@Composable
fun SharedCostPerPersonCard(
    perPersonAmount: String,
    participants: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    perPersonEyebrow: String,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, colors.line), shape)
            .background(colors.surface)
            .padding(dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            Text(
                text = perPersonEyebrow,
                style = typography.eyebrow,
                color = colors.muted,
            )
            Text(
                text = perPersonAmount,
                style = typography.displayAmount,
                color = colors.primary,
            )
        }
        participants.forEachIndexed { index, (name, amount) ->
            SharedCostParticipantRow(
                index = index + 1,
                name = name,
                amount = amount,
                showNoteIcon = true,
            )
        }
    }
}

@Composable
fun SharedCostCustomSplitCard(
    participants: List<Pair<String, String>>,
    onShareChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val shape = ProExpenseTheme.shapes.card

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, colors.line), shape)
            .background(colors.surface)
            .padding(horizontal = dimens.cardPadding),
    ) {
        participants.forEachIndexed { index, (name, amount) ->
            SharedCostParticipantRow(
                index = index + 1,
                name = name,
                amount = amount.removePrefix("$"),
                editableAmount = true,
                onAmountChange = { onShareChange(index, it) },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SharedCostPeopleCardPreview() {
    ProExpenseTheme {
        val dimens = ProExpenseTheme.dimensions
        SharedCostPeopleCard(
            count = 4,
            onDecrement = {},
            onIncrement = {},
            modifier = Modifier.padding(dimens.space16),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SharedCostParticipantRowPreview() {
    ProExpenseTheme {
        val dimens = ProExpenseTheme.dimensions
        Column(Modifier.padding(dimens.space16)) {
            SharedCostParticipantRow(
                index = 1,
                name = "Aiko",
                amount = "$30",
                showNoteIcon = true,
            )
        }
    }
}
