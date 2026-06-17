package com.arduia.expense.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

private val InactiveDotColor = Color(0x2E2B1F17)

@Composable
fun OnboardingTopBar(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    showSkip: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (showSkip) {
            Text(
                text = "Skip",
                modifier = Modifier
                    .clickable(onClick = onSkip)
                    .padding(4.dp)
                    .semantics { contentDescription = "Skip onboarding" },
                style = typography.onboardingNav,
                color = colors.muted,
            )
        }
    }
}

@Composable
fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (selected) 22.dp else 6.dp,
                animationSpec = tween(durationMillis = 200),
                label = "onboardingDotWidth",
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(if (selected) RoundedCornerShape(99.dp) else CircleShape)
                    .background(if (selected) colors.primary else InactiveDotColor)
                    .semantics {
                        contentDescription = if (selected) {
                            "Page ${index + 1} of $pageCount, current"
                        } else {
                            "Page ${index + 1} of $pageCount"
                        }
                    },
            )
        }
    }
}

@Composable
fun OnboardingNavRow(
    currentPage: Int,
    pageCount: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val showBack = currentPage > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(bottom = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "‹ Back",
                modifier = Modifier
                    .alpha(if (showBack) 1f else 0f)
                    .then(
                        if (showBack) {
                            Modifier.clickable(onClick = onBack)
                        } else {
                            Modifier
                        },
                    )
                    .semantics { contentDescription = "Back" },
                style = typography.onboardingNav,
                color = colors.muted,
            )
        }
        OnboardingPageIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = "Next ›",
                modifier = Modifier
                    .clickable(onClick = onNext)
                    .semantics { contentDescription = "Next" },
                style = typography.onboardingNavEmphasis,
                color = colors.ink,
            )
        }
    }
}
