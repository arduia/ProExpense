package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileStepHeader(
    step: Int,
    totalSteps: Int,
    eyebrow: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    skipLabel: String = "Skip",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileStepProgressBar(currentStep = step, totalSteps = totalSteps)
            if (onSkip != null) {
                ProTextAction(
                    text = skipLabel,
                    onClick = onSkip,
                    style = typography.navAction,
                    color = colors.muted,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            Text(
                text = eyebrow,
                style = typography.eyebrow,
                color = colors.primary,
            )
            Text(
                text = title,
                style = typography.profileScreenTitle.copy(fontFamily = typography.serifFamily),
                color = colors.onSurface,
            )
            Text(
                text = subtitle,
                style = typography.body,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileStepProgressBar(
    currentStep: Int,
    totalSteps: Int,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val stepNumber = index + 1
            val active = stepNumber <= currentStep
            if (stepNumber == currentStep) {
                Box(
                    modifier = Modifier
                        .width(dimens.profileStepBarWidth)
                        .height(dimens.profileStepBarHeight)
                        .clip(CircleShape)
                        .background(colors.primary),
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(dimens.pageIndicatorDotSize)
                        .height(dimens.pageIndicatorDotSize)
                        .clip(CircleShape)
                        .background(
                            if (active) colors.primary else colors.onSurface.copy(alpha = 0.18f),
                        ),
                )
            }
        }
    }
}

@Preview(
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun ProfileStepHeaderPreview() {
    ProExpenseTheme {
        ProfileStepHeader(
            step = 1,
            totalSteps = 2,
            eyebrow = "PROFILE · 1 OF 2",
            title = "Set up your profile",
            subtitle = "Your name personalizes the app — greeting you on the home screen and identifying your records and exports.",
            onSkip = {},
            modifier = Modifier.padding(ProExpenseTheme.dimensions.screenPadding),
        )
    }
}
