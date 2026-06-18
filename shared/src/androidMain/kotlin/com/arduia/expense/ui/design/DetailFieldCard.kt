package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DetailFieldCard(
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .padding(dimens.space14),
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        trailing?.invoke()
    }
}

@Composable
fun DetailNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    placeholder: String = "",
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
        trailing = {
            Text(
                text = "${value.length}/$maxLength",
                style = typography.caption,
                color = colors.muted,
            )
        },
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = typography.body.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            singleLine = true,
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(text = placeholder, style = typography.body, color = colors.muted)
                }
                inner()
            },
        )
    }
}

@Composable
fun DetailAmountSummaryCard(
    amountLabel: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .padding(dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "AMOUNT", style = typography.eyebrow, color = colors.muted)
            Row(
                modifier = Modifier.clickableWithoutRipple(onEdit),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space4),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Back,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconInline,
                )
                Text(text = "Edit", style = typography.bodyMedium, color = colors.primary)
            }
        }
        Text(text = amountLabel, style = typography.sectionHead, color = colors.onSurface)
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier =
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick,
    )
