package com.arduia.expense.ui.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProCircularProgress(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    strokeWidth: Dp = 3.5.dp,
) {
    val color = ProExpenseTheme.colors.primary
    Canvas(modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val diameter = this.size.minDimension - stroke
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

/** Two-step profile setup progress — active segment + dot per design spec. */
@Composable
fun ProfileStepProgress(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dims.profileProgressGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val step = index + 1
            val active = step <= currentStep
            if (step == currentStep) {
                Box(
                    modifier = Modifier
                        .width(dims.profileProgressBarWidth)
                        .height(dims.profileProgressBarHeight)
                        .clip(shapes.pill)
                        .background(colors.primary),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(dims.profileProgressDotSize)
                        .clip(shapes.pill)
                        .background(if (active) colors.primary else colors.lineStrong),
                )
            }
        }
    }
}

/** @deprecated Use [ProfileStepProgress] */
@Composable
fun ProLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val step = when {
        progress >= 1f -> 2
        progress > 0f -> 1
        else -> 0
    }
    ProfileStepProgress(currentStep = step, totalSteps = 2, modifier = modifier)
}
