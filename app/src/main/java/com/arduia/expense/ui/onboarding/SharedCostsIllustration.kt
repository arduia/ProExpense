package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.arduia.expense.ui.theme.ProExpensePalette

private val Blue50 = Color(0xFFE1F5FE)

@Composable
fun SharedCostsIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val stroke = w * 0.018f

        drawCircle(color = Blue50, radius = w * 0.42f, center = Offset(cx, cy))

        val receiptW = w * 0.38f
        val receiptH = h * 0.34f
        val receiptLeft = cx - receiptW / 2f
        val receiptTop = cy - receiptH * 0.55f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(receiptLeft, receiptTop),
            size = Size(receiptW, receiptH),
            cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
        )
        drawRoundRect(
            color = ProExpensePalette.Ink,
            topLeft = Offset(receiptLeft, receiptTop),
            size = Size(receiptW, receiptH),
            cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
            style = Stroke(width = stroke),
        )

        repeat(2) { index ->
            val y = receiptTop + receiptH * (0.22f + index * 0.12f)
            drawLine(
                color = ProExpensePalette.Gray300,
                start = Offset(receiptLeft + receiptW * 0.15f, y),
                end = Offset(receiptLeft + receiptW * 0.85f, y),
                strokeWidth = stroke * 0.45f,
                cap = StrokeCap.Round,
            )
        }

        val avatarRadius = w * 0.055f
        val avatarY = receiptTop + receiptH + h * 0.1f
        val avatarXs = listOf(cx - w * 0.22f, cx, cx + w * 0.22f)
        val avatarColors = listOf(
            ProExpensePalette.Blue500,
            ProExpensePalette.Blue300,
            ProExpensePalette.Blue700,
        )

        avatarXs.forEachIndexed { index, avatarX ->
            val avatarCenter = Offset(avatarX, avatarY)
            val receiptBottom = Offset(cx, receiptTop + receiptH)
            drawLine(
                color = ProExpensePalette.Blue300,
                start = receiptBottom,
                end = avatarCenter,
                strokeWidth = stroke * 0.6f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(stroke * 2f, stroke * 2f)),
            )
            drawCircle(color = avatarColors[index], radius = avatarRadius, center = avatarCenter)
            drawCircle(
                color = Color.White,
                radius = avatarRadius * 0.35f,
                center = Offset(avatarCenter.x, avatarCenter.y - avatarRadius * 0.12f),
            )
            drawArc(
                color = Color.White,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(avatarCenter.x - avatarRadius * 0.5f, avatarCenter.y),
                size = Size(avatarRadius, avatarRadius * 0.8f),
                style = Stroke(width = stroke * 0.6f, cap = StrokeCap.Round),
            )

            val pillW = w * 0.12f
            val pillH = h * 0.04f
            val pillTop = avatarY + avatarRadius + h * 0.02f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(avatarX - pillW / 2f, pillTop),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(pillH / 2f, pillH / 2f),
            )
            drawRoundRect(
                color = ProExpensePalette.Ink,
                topLeft = Offset(avatarX - pillW / 2f, pillTop),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(pillH / 2f, pillH / 2f),
                style = Stroke(width = stroke * 0.6f),
            )
        }
    }
}
