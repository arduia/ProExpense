package com.arduia.expense.ui.design

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PageDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dims.pageDotSpacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val active = i == activeIndex
            val width by animateDpAsState(
                targetValue = if (active) dims.pageDotActiveWidth else dims.pageDotSize,
                animationSpec = tween(
                    durationMillis = ProExpenseTheme.motion.screenTransitionMs,
                    easing = ProExpenseTheme.motion.standardEasing,
                ),
                label = "dotWidth",
            )
            val color by animateColorAsState(
                targetValue = if (active) colors.primary else colors.lineStrong,
                label = "dotColor",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(dims.pageDotSize)
                    .clip(shapes.pill)
                    .background(color),
            )
        }
    }
}
