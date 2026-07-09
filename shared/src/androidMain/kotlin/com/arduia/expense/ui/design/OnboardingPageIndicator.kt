package com.arduia.expense.ui.design

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val active = index == currentPage
            val width by animateDpAsState(
                targetValue = if (active) dimens.pageIndicatorActiveWidth else dimens.pageIndicatorDotSize,
                animationSpec =
                    tween(
                        durationMillis = motion.screenDurationMillis,
                        easing = motion.standardEasing,
                    ),
                label = "onboarding-dot-width",
            )
            Box(
                modifier =
                    Modifier
                        .width(width)
                        .height(dimens.pageIndicatorDotSize)
                        .clip(CircleShape)
                        .background(
                            if (active) colors.primary else colors.onSurface.copy(alpha = 0.18f),
                        ),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 120)
@Composable
private fun OnboardingPageIndicatorPreview() {
    ProExpenseTheme {
        OnboardingPageIndicator(pageCount = 5, currentPage = 2)
    }
}
