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
fun JournalIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val stroke = w * 0.018f

        drawCircle(color = Blue50, radius = w * 0.42f, center = Offset(cx, cy))

        val cardW = w * 0.52f
        val cardH = h * 0.14f
        val cardLeft = cx - cardW / 2f
        val offsets = listOf(h * 0.06f, h * 0.03f, 0f)
        val borderWidths = listOf(stroke * 0.6f, stroke, stroke * 1.3f)

        offsets.forEachIndexed { index, yOffset ->
            val cardTop = cy - cardH * 0.35f + yOffset
            val isFront = index == 2
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cardLeft, cardTop),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
            )
            drawRoundRect(
                color = ProExpensePalette.Ink,
                topLeft = Offset(cardLeft, cardTop),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
                style = Stroke(width = borderWidths[index]),
            )

            if (isFront) {
                val checkRadius = cardH * 0.14f
                val checkCenter = Offset(cardLeft + cardW * 0.12f, cardTop + cardH * 0.55f)
                drawCircle(color = ProExpensePalette.Blue500, radius = checkRadius, center = checkCenter)
                val checkPath = Path().apply {
                    moveTo(checkCenter.x - checkRadius * 0.35f, checkCenter.y)
                    lineTo(checkCenter.x - checkRadius * 0.05f, checkCenter.y + checkRadius * 0.32f)
                    lineTo(checkCenter.x + checkRadius * 0.42f, checkCenter.y - checkRadius * 0.28f)
                }
                drawPath(
                    path = checkPath,
                    color = Color.White,
                    style = Stroke(width = stroke * 1.1f, cap = StrokeCap.Round),
                )

                repeat(2) { lineIndex ->
                    val lineY = cardTop + cardH * (0.35f + lineIndex * 0.18f)
                    val lineW = cardW * if (lineIndex == 0) 0.42f else 0.28f
                    drawLine(
                        color = ProExpensePalette.Gray300,
                        start = Offset(cardLeft + cardW * 0.24f, lineY),
                        end = Offset(cardLeft + cardW * 0.24f + lineW, lineY),
                        strokeWidth = stroke * 0.45f,
                        cap = StrokeCap.Round,
                    )
                }
            } else {
                val dotRadius = cardH * 0.1f
                drawCircle(
                    color = if (index == 1) ProExpensePalette.Blue100 else ProExpensePalette.Gray200,
                    radius = dotRadius,
                    center = Offset(cardLeft + cardW * 0.12f, cardTop + cardH * 0.55f),
                )
                drawLine(
                    color = ProExpensePalette.Gray300,
                    start = Offset(cardLeft + cardW * 0.24f, cardTop + cardH * 0.45f),
                    end = Offset(cardLeft + cardW * 0.62f, cardTop + cardH * 0.45f),
                    strokeWidth = stroke * 0.45f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
