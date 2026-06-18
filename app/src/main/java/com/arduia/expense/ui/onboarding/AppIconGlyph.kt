package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpensePalette

@Composable
fun AppIconGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    cornerRadius: Dp = 24.dp,
) {
    val tileColor = ProExpensePalette.Blue500
    val glyphColor = Color.White

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val radius = cornerRadius.toPx()
        val stroke = w * 0.045f

        drawRoundRect(
            color = tileColor.copy(alpha = 0.30f),
            topLeft = Offset(w * 0.04f, h * 0.14f),
            size = Size(w * 0.92f, h * 0.92f),
            cornerRadius = CornerRadius(radius * 1.1f, radius * 1.1f),
        )

        drawRoundRect(
            color = tileColor,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(radius, radius),
        )

        val inset = w * 0.28f
        val frameSize = w - inset * 2f
        drawRoundRect(
            color = glyphColor,
            topLeft = Offset(inset, inset),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = Stroke(width = stroke),
        )

        val dotRadius = w * 0.04f
        val corners = listOf(
            Offset(inset, inset),
            Offset(inset + frameSize, inset),
            Offset(inset, inset + frameSize),
            Offset(inset + frameSize, inset + frameSize),
        )
        corners.forEach { corner ->
            drawCircle(color = glyphColor, radius = dotRadius, center = corner)
        }
    }
}
