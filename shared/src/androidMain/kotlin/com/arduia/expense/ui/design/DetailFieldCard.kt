package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DetailFieldCard(
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val shape = ProExpenseTheme.shapes.card
    val strokeColor = borderColor ?: colors.line

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.proClickable(onClick = onClick, shape = shape)
                    } else {
                        Modifier
                    },
                ).clip(shape)
                .border(BorderStroke(1.dp, strokeColor), shape)
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
    atLimit: Boolean = false,
    errorMessage: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val borderColor = if (atLimit) colors.danger else colors.line
    val counterColor = if (atLimit) colors.danger else colors.muted

    Column(modifier = modifier.fillMaxWidth()) {
        DetailFieldCard(
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
                    color = counterColor,
                )
            },
            borderColor = borderColor,
        ) {
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= maxLength) onValueChange(it) },
                textStyle = typography.body.copy(color = colors.onSurface),
                cursorBrush = SolidColor(colors.primary),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = typography.body, color = colors.muted)
                    }
                    inner()
                },
            )
        }
        if (errorMessage != null) {
            Row(
                modifier = Modifier.padding(top = ProExpenseTheme.dimensions.space6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.dimensions.space4),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Close,
                    contentDescription = null,
                    tint = colors.danger,
                    size = ProExpenseTheme.dimensions.iconInline,
                )
                Text(
                    text = errorMessage,
                    style = typography.caption,
                    color = colors.danger,
                )
            }
        }
    }
}

@Composable
fun DetailDateTimeField(
    dateLabel: String,
    timeLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    DetailFieldCard(
        modifier = modifier,
        onClick = onClick,
        leading = {
            ProIcon(
                glyph = ProIconGlyph.Calendar,
                contentDescription = null,
                tint = colors.muted,
                size = ProExpenseTheme.dimensions.iconNav,
            )
        },
        trailing = {
            ProIcon(
                glyph = ProIconGlyph.ChevronRight,
                contentDescription = null,
                tint = colors.muted,
                size = ProExpenseTheme.dimensions.iconInline,
            )
        },
    ) {
        Column {
            Text(text = dateLabel, style = typography.bodyMedium, color = colors.onSurface)
            Text(
                text = timeLabel,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = ProExpenseTheme.dimensions.space2),
            )
        }
    }
}

@Composable
fun DetailTagField(
    tagLabel: String?,
    placeholder: String,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val hasTag = !tagLabel.isNullOrBlank()

    DetailFieldCard(
        modifier = modifier,
        onClick = onClick,
        leading = {
            ProIcon(
                glyph = ProIconGlyph.At,
                contentDescription = null,
                tint = if (hasTag) colors.tag else colors.muted,
                size = dimens.iconNav,
            )
        },
        trailing =
            if (hasTag) {
                {
                    Text(
                        text = "Clear",
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                        modifier =
                            Modifier
                                .clip(ProExpenseTheme.shapes.chip)
                                .proClickable(onClick = onClear, shape = ProExpenseTheme.shapes.chip)
                                .padding(horizontal = dimens.space8, vertical = dimens.space4),
                    )
                }
            } else {
                null
            },
    ) {
        Text(
            text = tagLabel ?: placeholder,
            style = typography.body,
            color = if (hasTag) colors.tag else colors.muted,
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
        modifier =
            modifier
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
            Text(text = "AMOUNT", style = typography.eyebrow, color = colors.onSurfaceMuted)
            Row(
                modifier =
                    Modifier
                        .proClickable(onClick = onEdit, shape = ProExpenseTheme.shapes.chip)
                        .padding(horizontal = dimens.space4, vertical = dimens.space2),
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
        Text(
            text = amountLabel,
            style = typography.detailsAmount,
            color = colors.onSurface,
        )
    }
}
