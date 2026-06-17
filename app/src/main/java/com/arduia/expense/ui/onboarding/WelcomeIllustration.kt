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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpensePalette

private val Blue50 = Color(0xFFE1F5FE)

@Composable
fun WelcomeIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        drawCircle(
            color = Blue50,
            radius = w * 0.42f,
            center = Offset(cx, cy),
        )

        val bookW = w * 0.62f
        val bookH = h * 0.42f
        val bookLeft = cx - bookW / 2f
        val bookTop = cy - bookH / 2f + h * 0.04f
        val spineX = cx
        val pageInset = bookW * 0.06f
        val stroke = w * 0.018f

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(bookLeft, bookTop),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
        drawRoundRect(
            color = ProExpensePalette.Ink,
            topLeft = Offset(bookLeft, bookTop),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
            style = Stroke(width = stroke),
        )
        drawLine(
            color = ProExpensePalette.Ink,
            start = Offset(spineX, bookTop + pageInset * 0.5f),
            end = Offset(spineX, bookTop + bookH - pageInset * 0.5f),
            strokeWidth = stroke * 0.8f,
        )

        val lineYStart = bookTop + bookH * 0.28f
        val lineSpacing = bookH * 0.1f
        repeat(3) { index ->
            val y = lineYStart + index * lineSpacing
            drawLine(
                color = ProExpensePalette.Gray300,
                start = Offset(bookLeft + pageInset, y),
                end = Offset(spineX - pageInset * 0.6f, y),
                strokeWidth = stroke * 0.45f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = ProExpensePalette.Gray300,
                start = Offset(spineX + pageInset * 0.6f, y + lineSpacing * 0.35f),
                end = Offset(bookLeft + bookW - pageInset, y + lineSpacing * 0.35f),
                strokeWidth = stroke * 0.45f,
                cap = StrokeCap.Round,
            )
        }

        val coinRadius = bookH * 0.11f
        val coinCenter = Offset(bookLeft + bookW * 0.28f, bookTop + bookH * 0.52f)
        drawCircle(color = ProExpensePalette.Blue500, radius = coinRadius, center = coinCenter)
        drawCircle(
            color = Color.White,
            radius = coinRadius * 0.55f,
            center = coinCenter,
            style = Stroke(width = stroke * 0.7f),
        )

        val checkRadius = bookH * 0.14f
        val checkCenter = Offset(bookLeft + bookW * 0.72f, bookTop + bookH * 0.38f)
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

        val sparkleCenter = Offset(bookLeft + bookW * 0.88f, bookTop + bookH * 0.12f)
        val sparkleSize = w * 0.035f
        drawLine(
            color = ProExpensePalette.Blue300,
            start = Offset(sparkleCenter.x, sparkleCenter.y - sparkleSize),
            end = Offset(sparkleCenter.x, sparkleCenter.y + sparkleSize),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ProExpensePalette.Blue300,
            start = Offset(sparkleCenter.x - sparkleSize, sparkleCenter.y),
            end = Offset(sparkleCenter.x + sparkleSize, sparkleCenter.y),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round,
        )

        val plusCenter = Offset(bookLeft + bookW * 0.93f, bookTop + bookH * 0.55f)
        val plusSize = w * 0.025f
        drawCircle(color = ProExpensePalette.Blue500, radius = plusSize * 1.4f, center = plusCenter)
        drawLine(
            color = Color.White,
            start = Offset(plusCenter.x, plusCenter.y - plusSize),
            end = Offset(plusCenter.x, plusCenter.y + plusSize),
            strokeWidth = stroke * 0.7f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(plusCenter.x - plusSize, plusCenter.y),
            end = Offset(plusCenter.x + plusSize, plusCenter.y),
            strokeWidth = stroke * 0.7f,
            cap = StrokeCap.Round,
        )

        drawOval(
            color = ProExpensePalette.Blue100,
            topLeft = Offset(bookLeft + bookW * 0.08f, bookTop - bookH * 0.08f),
            size = Size(bookW * 0.18f, bookH * 0.12f),
            style = Fill,
        )
    }
}
