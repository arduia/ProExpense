package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.arduia.expense.ui.theme.ProExpensePalette

private val Blue50 = Color(0xFFE1F5FE)

@Composable
fun EventBudgetIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val stroke = w * 0.018f

        drawCircle(color = Blue50, radius = w * 0.42f, center = Offset(cx, cy))

        val calW = w * 0.22f
        val calH = h * 0.2f
        val calLeft = cx - w * 0.28f
        val calTop = cy - calH * 0.35f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(calLeft, calTop),
            size = Size(calW, calH),
            cornerRadius = CornerRadius(w * 0.025f, w * 0.025f),
        )
        drawRoundRect(
            color = ProExpensePalette.Ink,
            topLeft = Offset(calLeft, calTop),
            size = Size(calW, calH),
            cornerRadius = CornerRadius(w * 0.025f, w * 0.025f),
            style = Stroke(width = stroke),
        )
        drawCircle(
            color = ProExpensePalette.Blue500,
            radius = w * 0.018f,
            center = Offset(calLeft + calW / 2f, calTop + calH * 0.55f),
        )

        val poleX = calLeft + calW * 0.15f
        drawLine(
            color = ProExpensePalette.Ink,
            start = Offset(poleX, calTop - h * 0.12f),
            end = Offset(poleX, calTop),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        val flagPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(poleX, calTop - h * 0.12f)
            lineTo(poleX + w * 0.1f, calTop - h * 0.09f)
            lineTo(poleX, calTop - h * 0.06f)
            close()
        }
        drawPath(flagPath, color = ProExpensePalette.Blue500)

        val ringRadius = w * 0.14f
        val ringCenter = Offset(cx + w * 0.18f, cy - h * 0.02f)
        drawCircle(
            color = ProExpensePalette.Gray300,
            radius = ringRadius,
            center = ringCenter,
            style = Stroke(width = stroke * 1.2f),
        )
        drawArc(
            color = ProExpensePalette.Blue500,
            startAngle = -90f,
            sweepAngle = 360f * 0.62f,
            useCenter = false,
            topLeft = Offset(ringCenter.x - ringRadius, ringCenter.y - ringRadius),
            size = Size(ringRadius * 2f, ringRadius * 2f),
            style = Stroke(width = stroke * 1.2f, cap = StrokeCap.Round),
        )

        val plusCenter = Offset(ringCenter.x, ringCenter.y - ringRadius - h * 0.04f)
        val plusSize = w * 0.02f
        drawLine(
            color = ProExpensePalette.Blue500,
            start = Offset(plusCenter.x, plusCenter.y - plusSize),
            end = Offset(plusCenter.x, plusCenter.y + plusSize),
            strokeWidth = stroke * 0.8f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ProExpensePalette.Blue500,
            start = Offset(plusCenter.x - plusSize, plusCenter.y),
            end = Offset(plusCenter.x + plusSize, plusCenter.y),
            strokeWidth = stroke * 0.8f,
            cap = StrokeCap.Round,
        )
    }
}
