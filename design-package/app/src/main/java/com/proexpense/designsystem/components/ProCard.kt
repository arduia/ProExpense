package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.proexpense.designsystem.theme.Line
import com.proexpense.designsystem.theme.White

/* ProCard — white surface, hairline border, soft shadow. NO M3 tonal tint.
 * See card-surfaces.md. */
@Composable
fun ProCard(
    modifier: Modifier = Modifier,
    radius: Dp = 18.dp,
    padding: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    var m = modifier
        .softShadow(elevation = 6.dp, shape = shape)
        .clip(shape)
        .background(White)
        .border(1.dp, Line, shape)
    if (onClick != null) m = m.pressScale(onClick = onClick)
    Box(modifier = m.padding(padding)) { content() }
}
