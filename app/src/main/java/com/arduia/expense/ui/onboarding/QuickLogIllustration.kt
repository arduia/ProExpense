package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.arduia.expense.ui.theme.ProExpensePalette

private val Blue50 = Color(0xFFE1F5FE)

@Composable
fun QuickLogIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val stroke = w * 0.018f

        drawCircle(color = Blue50, radius = w * 0.42f, center = Offset(cx, cy))

        val phoneW = w * 0.42f
        val phoneH = h * 0.52f
        val phoneLeft = cx - phoneW / 2f
        val phoneTop = cy - phoneH / 2f + h * 0.02f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
        )
        drawRoundRect(
            color = ProExpensePalette.Ink,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = Stroke(width = stroke),
        )

        val headerH = phoneH * 0.22f
        drawRoundRect(
            color = ProExpensePalette.Blue500,
            topLeft = Offset(phoneLeft + stroke, phoneTop + stroke),
            size = Size(phoneW - stroke * 2f, headerH),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )

        val rowY = phoneTop + headerH + phoneH * 0.12f
        val rowW = phoneW * 0.62f
        val rowLeft = phoneLeft + (phoneW - rowW) / 2f
        drawRoundRect(
            color = ProExpensePalette.Blue100,
            topLeft = Offset(rowLeft, rowY),
            size = Size(rowW, phoneH * 0.07f),
            cornerRadius = CornerRadius(phoneH * 0.035f, phoneH * 0.035f),
        )
        drawRoundRect(
            color = ProExpensePalette.Gray300,
            topLeft = Offset(rowLeft, rowY + phoneH * 0.11f),
            size = Size(rowW * 0.75f, phoneH * 0.07f),
            cornerRadius = CornerRadius(phoneH * 0.035f, phoneH * 0.035f),
        )

        val plusRadius = phoneH * 0.1f
        val plusCenter = Offset(phoneLeft + phoneW * 0.88f, phoneTop + phoneH * 0.78f)
        drawCircle(color = ProExpensePalette.Blue500, radius = plusRadius, center = plusCenter)
        val plusSize = plusRadius * 0.45f
        drawLine(
            color = Color.White,
            start = Offset(plusCenter.x, plusCenter.y - plusSize),
            end = Offset(plusCenter.x, plusCenter.y + plusSize),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(plusCenter.x - plusSize, plusCenter.y),
            end = Offset(plusCenter.x + plusSize, plusCenter.y),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round,
        )

        val lineX = phoneLeft - w * 0.12f
        repeat(3) { index ->
            val lineLen = w * (0.08f + index * 0.03f)
            val y = phoneTop + phoneH * (0.35f + index * 0.08f)
            drawLine(
                color = ProExpensePalette.Blue300,
                start = Offset(lineX - lineLen, y),
                end = Offset(lineX, y),
                strokeWidth = stroke * 0.7f,
                cap = StrokeCap.Round,
            )
        }

        val boltCenter = Offset(lineX - w * 0.04f, phoneTop + phoneH * 0.72f)
        val boltPath = Path().apply {
            moveTo(boltCenter.x, boltCenter.y - w * 0.04f)
            lineTo(boltCenter.x + w * 0.025f, boltCenter.y)
            lineTo(boltCenter.x, boltCenter.y)
            lineTo(boltCenter.x + w * 0.03f, boltCenter.y + w * 0.05f)
            lineTo(boltCenter.x - w * 0.01f, boltCenter.y + w * 0.01f)
            lineTo(boltCenter.x + w * 0.01f, boltCenter.y - w * 0.01f)
            close()
        }
        drawPath(
            path = boltPath,
            color = ProExpensePalette.Blue500,
            style = Stroke(width = stroke * 0.8f, cap = StrokeCap.Round),
        )
    }
}
