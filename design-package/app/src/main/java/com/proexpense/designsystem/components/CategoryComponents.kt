package com.proexpense.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.model.Category
import com.proexpense.designsystem.theme.Gray300
import com.proexpense.designsystem.theme.Ink2
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.OnFilled

/* CategoryBadge — circular tinted glyph used in list rows. See category.md. */
@Composable
fun CategoryBadge(
    category: Category,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 38.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(category.tint),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.label,
            tint = category.accent,
            modifier = Modifier.size(size * 0.52f),
        )
    }
}

/* CategoryChip — selectable, single-select group. Selected = accent fill. */
@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = if (selected) category.accent else androidx.compose.ui.graphics.Color.Transparent
    val content = if (selected) OnFilled else Ink2
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(container)
            .border(1.2.dp, if (selected) category.accent else Gray300, CircleShape)
            .pressScale(onClick = onClick)
            .padding(start = 8.dp, top = 7.dp, end = 12.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = if (selected) OnFilled else category.accent,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = category.label,
            color = content,
            fontFamily = Manrope,
            fontSize = 12.5.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
