package com.arduia.expense.ui.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Material determinate linear progress — mirrors `MdLinearProgress`.
 */
@Composable
fun ProLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(colors.primaryContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .height(4.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(colors.primary),
        )
    }
}

/**
 * Material circular progress snapshot — mirrors `MdCircularProgress`.
 */
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
