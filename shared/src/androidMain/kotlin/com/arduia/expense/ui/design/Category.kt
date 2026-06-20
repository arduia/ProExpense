package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun LogCategoryBadge(
    categoryId: String,
    modifier: Modifier = Modifier,
    size: Dp = ProExpenseTheme.dimensions.iconBadge,
) {
    val colors = ProExpenseTheme.colors.category(categoryId)
    val iconSize = size * 0.52f
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.tint),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = categoryIcon(categoryId),
            contentDescription = null,
            tint = colors.accent,
            size = iconSize,
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    categoryId: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val category = colors.category(categoryId)
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val chipShape = ProExpenseTheme.shapes.chip
    val background = if (selected) category.accent else Color.Transparent
    val contentColor = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant
    val borderColor = if (selected) category.accent else colors.lineStrong
    val textStyle = (if (selected) typography.bodySemiBold else typography.bodyMedium)
        .copy(fontSize = 12.5.sp)

    Row(
        modifier = modifier
            .proPressScale(interactionSource)
            .clip(chipShape)
            .background(background)
            .border(BorderStroke(dimens.chipBorderWidth, borderColor), chipShape)
            .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
            .padding(start = dimens.space8, top = 7.dp, end = dimens.space12, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
    ) {
        ProIcon(
            glyph = categoryIcon(categoryId),
            contentDescription = null,
            tint = if (selected) colors.onPrimaryWarm else category.accent,
            size = dimens.iconChipLeading,
        )
        Text(text = label, style = textStyle, color = contentColor)
    }
}
