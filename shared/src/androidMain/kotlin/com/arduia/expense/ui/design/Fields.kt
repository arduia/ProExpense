package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileNameField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.searchField)
            .border(BorderStroke(1.dp, colors.primary), ProExpenseTheme.shapes.searchField)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.User,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
                Box(modifier = Modifier.padding(start = dimens.space8)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = typography.body, color = colors.muted)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.searchField)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.searchField)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Search,
                    contentDescription = null,
                    tint = colors.muted,
                    size = dimens.iconInline,
                )
                Box(modifier = Modifier.padding(start = dimens.space8)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = typography.body, color = colors.muted)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) motion.pressedScale else 1f
    val background = if (selected) colors.onSurface else Color.Transparent
    val contentColor = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant
    val borderColor = if (selected) colors.onSurface else colors.lineStrong
    val textStyle = if (selected) typography.bodySemiBold else typography.bodyMedium

    Text(
        text = label,
        style = textStyle,
        color = contentColor,
        modifier = modifier
            .scale(scale)
            .clip(ProExpenseTheme.shapes.chip)
            .background(background)
            .border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.chip)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = dimens.space14, vertical = dimens.space8),
    )
}
