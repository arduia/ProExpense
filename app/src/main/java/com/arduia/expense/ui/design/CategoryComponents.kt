package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryBadge(
    label: String,
    style: CategoryStyle,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
) {
    Surface(
        modifier = modifier.defaultMinSize(minWidth = size, minHeight = size),
        shape = ProExpenseTheme.shapes.badge,
        color = style.tint,
        contentColor = style.accent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label.take(1).uppercase(),
                style = ProExpenseTheme.typography.bodyEmphasis,
                color = style.accent,
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = ProExpenseTheme.colors.primary,
) {
    val colors = ProExpenseTheme.colors
    val background = if (selected) accent else Color.Transparent
    val content = if (selected) colors.paperWarm else colors.ink2
    val borderColor = if (selected) accent else colors.lineStrong

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ProExpenseTheme.shapes.pill,
        color = background,
        contentColor = content,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = ProExpenseTheme.typography.bodyEmphasis,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
