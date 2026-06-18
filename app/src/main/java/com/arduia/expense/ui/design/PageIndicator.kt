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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Worm-style page indicator (Material `MdPageDots`). The active page stretches
 * into a 22dp pill in the primary blue; the rest are 7dp outline-variant dots.
 */
@Composable
fun PageDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val active = i == activeIndex
            val width by animateDpAsState(
                targetValue = if (active) 22.dp else 7.dp,
                animationSpec = tween(
                    durationMillis = ProExpenseTheme.motion.screenTransitionMs,
                    easing = ProExpenseTheme.motion.standardEasing,
                ),
                label = "dotWidth",
            )
            val color by animateColorAsState(
                targetValue = if (active) colors.primary else colors.outlineVariant,
                label = "dotColor",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(7.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(color),
            )
        }
    }
}
