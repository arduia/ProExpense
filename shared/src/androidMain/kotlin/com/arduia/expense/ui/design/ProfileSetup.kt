package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileSetupHeader(
    step: Int,
    totalSteps: Int,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dims.topBarHeight)
            .padding(horizontal = dims.space20),
    ) {
        ProfileStepProgress(
            currentStep = step,
            totalSteps = totalSteps,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        ProButton(
            text = "Skip",
            onClick = onSkip,
            variant = ProButtonVariant.Ghost,
            size = ProButtonSize.Md,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
fun ProfileEyebrow(
    step: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    Text(
        text = "PROFILE · $step OF $totalSteps",
        style = ProExpenseTheme.typography.eyebrow,
        color = colors.primary,
        modifier = modifier,
    )
}
