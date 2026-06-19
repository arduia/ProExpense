package com.arduia.expense.ui.onboarding

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect as UiRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProColors
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlin.math.min

private val illustrationWidth = 300.dp
private val illustrationHeight = 280.dp
private val haloDiameter = 220.dp

@Composable
private fun OnboardingIllustrationFrame(
    modifier: Modifier = Modifier,
    drawScene: DrawScope.(Size) -> Unit,
) {
    val colors = ProExpenseTheme.colors

    Box(
        modifier = modifier.size(illustrationWidth, illustrationHeight),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(haloDiameter)) {
            drawCircle(color = colors.primaryTint.copy(alpha = 0.55f))
        }
        Canvas(Modifier.fillMaxSize()) {
            drawScene(size)
        }
    }
}

private fun DrawScope.drawLabel(
    text: String,
    center: Offset,
    color: Color,
    fontSizeSp: Float,
    bold: Boolean = false,
    mono: Boolean = false,
) {
    val paint = Paint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        textSize = fontSizeSp
        textAlign = Paint.Align.CENTER
        typeface = when {
            mono -> Typeface.create(Typeface.MONOSPACE, if (bold) Typeface.BOLD else Typeface.NORMAL)
            bold -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    drawContext.canvas.nativeCanvas.drawText(
        text,
        center.x,
        center.y + bounds.height() / 2f,
        paint,
    )
}

private fun DrawScope.drawCheckmark(
    center: Offset,
    color: Color,
    arm: Dp = 8.dp,
    stroke: Dp = 2.5.dp,
) {
    val path = Path().apply {
        moveTo(center.x - arm.toPx(), center.y)
        lineTo(center.x - arm.toPx() * 0.25f, center.y + arm.toPx() * 0.85f)
        lineTo(center.x + arm.toPx(), center.y - arm.toPx() * 0.75f)
    }
    drawPath(path, color, style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round))
}

private fun DrawScope.drawPlus(
    center: Offset,
    color: Color,
    arm: Dp = 7.dp,
    stroke: Dp = 2.5.dp,
) {
    val half = arm.toPx()
    drawLine(color, Offset(center.x - half, center.y), Offset(center.x + half, center.y), stroke.toPx(), StrokeCap.Round)
    drawLine(color, Offset(center.x, center.y - half), Offset(center.x, center.y + half), stroke.toPx(), StrokeCap.Round)
}

private fun DrawScope.drawPersonIcon(center: Offset, radius: Dp, colors: ProColors) {
    drawCircle(color = colors.primary, radius = radius.toPx(), center = center)
    drawCircle(
        color = colors.surface,
        radius = radius.toPx() * 0.28f,
        center = Offset(center.x, center.y - radius.toPx() * 0.18f),
    )
    drawArc(
        color = colors.surface,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(center.x - radius.toPx() * 0.42f, center.y + radius.toPx() * 0.05f),
        size = Size(radius.toPx() * 0.84f, radius.toPx() * 0.55f),
        style = Stroke(width = radius.toPx() * 0.22f, cap = StrokeCap.Round),
    )
}

private fun receiptPath(left: Float, top: Float, width: Float, height: Float, tooth: Float): Path {
    return Path().apply {
        moveTo(left, top)
        lineTo(left + width, top)
        lineTo(left + width, top + height - tooth)
        val teeth = 6
        val step = width / teeth
        for (index in teeth downTo 0) {
            val x = left + index * step
            val y = if (index % 2 == 0) top + height else top + height - tooth
            lineTo(x, y)
        }
        close()
    }
}

@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationFrame(modifier) { size ->
        val w = size.width
        val h = size.height
        val bookW = w * 0.56f
        val bookH = h * 0.38f
        val left = (w - bookW) / 2f
        val top = h * 0.30f
        val radius = 14.dp.toPx()
        val stroke = 2.5.dp.toPx()

        drawCircle(color = colors.primary, radius = 7.dp.toPx(), center = Offset(w * 0.72f, h * 0.22f))

        drawRoundRect(
            color = colors.surface,
            topLeft = Offset(left, top),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = stroke),
        )
        drawRoundRect(
            color = colors.surface,
            topLeft = Offset(left + stroke, top + stroke),
            size = Size(bookW - stroke * 2, bookH - stroke * 2),
            cornerRadius = CornerRadius(radius, radius),
        )
        drawLine(
            color = colors.onSurface,
            start = Offset(left + bookW / 2f, top + 10.dp.toPx()),
            end = Offset(left + bookW / 2f, top + bookH - 10.dp.toPx()),
            strokeWidth = stroke,
        )

        val lineStart = left + bookW * 0.12f
        val lineEnd = left + bookW * 0.40f
        repeat(3) { index ->
            val y = top + bookH * (0.30f + index * 0.14f)
            drawLine(colors.muted2, Offset(lineStart, y), Offset(lineEnd, y), 2.dp.toPx(), StrokeCap.Round)
        }

        val badgeCx = left + bookW * 0.78f
        val badgeCy = top + bookH * 0.36f
        drawCircle(color = colors.primary, radius = 20.dp.toPx(), center = Offset(badgeCx, badgeCy))
        drawCheckmark(Offset(badgeCx, badgeCy), colors.onPrimaryWarm, arm = 7.dp)

        val noteY1 = top + bookH * 0.58f
        val noteY2 = top + bookH * 0.72f
        drawLine(colors.muted2, Offset(left + bookW * 0.56f, noteY1), Offset(left + bookW * 0.86f, noteY1), 2.dp.toPx(), StrokeCap.Round)
        drawLine(colors.muted2, Offset(left + bookW * 0.56f, noteY2), Offset(left + bookW * 0.78f, noteY2), 2.dp.toPx(), StrokeCap.Round)

        val coinCx = left - 4.dp.toPx()
        val coinCy = top + bookH - 6.dp.toPx()
        drawCircle(color = colors.surface, radius = 16.dp.toPx(), center = Offset(coinCx, coinCy), style = Stroke(width = stroke))
        drawCircle(color = colors.primary, radius = 11.dp.toPx(), center = Offset(coinCx, coinCy))
        drawLine(
            colors.onPrimaryWarm,
            Offset(coinCx - 5.dp.toPx(), coinCy),
            Offset(coinCx + 5.dp.toPx(), coinCy),
            2.dp.toPx(),
            StrokeCap.Round,
        )
        drawLine(
            colors.onPrimaryWarm,
            Offset(coinCx - 1.dp.toPx(), coinCy - 5.dp.toPx()),
            Offset(coinCx - 1.dp.toPx(), coinCy + 5.dp.toPx()),
            2.dp.toPx(),
            StrokeCap.Round,
        )

        val plusCenter = Offset(left + bookW + 2.dp.toPx(), top + bookH - 4.dp.toPx())
        drawCircle(color = colors.primary, radius = 11.dp.toPx(), center = plusCenter)
        drawPlus(plusCenter, colors.onPrimaryWarm, arm = 5.dp, stroke = 2.dp)
    }
}

@Composable
fun QuickLogIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationFrame(modifier) { size ->
        val w = size.width
        val h = size.height
        val cardW = w * 0.42f
        val cardH = h * 0.50f
        val left = w * 0.34f
        val top = h * 0.22f
        val radius = 16.dp.toPx()
        val stroke = 2.5.dp.toPx()

        repeat(3) { index ->
            val y = top + cardH * 0.42f + index * 10.dp.toPx()
            drawLine(
                colors.primarySoft,
                Offset(w * 0.12f, y),
                Offset(w * 0.12f + (18 - index * 4).dp.toPx(), y),
                3.dp.toPx(),
                StrokeCap.Round,
            )
        }

        val boltCx = w * 0.20f
        val boltCy = top + cardH * 0.72f
        val bolt = Path().apply {
            moveTo(boltCx, boltCy - 12.dp.toPx())
            lineTo(boltCx - 8.dp.toPx(), boltCy + 2.dp.toPx())
            lineTo(boltCx - 1.dp.toPx(), boltCy + 2.dp.toPx())
            lineTo(boltCx - 4.dp.toPx(), boltCy + 14.dp.toPx())
            lineTo(boltCx + 10.dp.toPx(), boltCy - 2.dp.toPx())
            lineTo(boltCx + 2.dp.toPx(), boltCy - 2.dp.toPx())
            close()
        }
        drawPath(bolt, colors.primary, style = Stroke(width = 2.dp.toPx()))
        drawPath(bolt, colors.primary)

        drawRoundRect(
            color = colors.surface,
            topLeft = Offset(left, top),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = stroke),
        )
        drawRoundRect(
            color = colors.primary,
            topLeft = Offset(left, top),
            size = Size(cardW, cardH * 0.28f),
            cornerRadius = CornerRadius(radius, radius),
        )
        drawLabel(
            text = "$12",
            center = Offset(left + cardW / 2f, top + cardH * 0.14f),
            color = colors.onPrimaryWarm,
            fontSizeSp = 22.dp.toPx(),
            bold = true,
            mono = true,
        )
        drawRoundRect(
            color = colors.primaryTint,
            topLeft = Offset(left + 14.dp.toPx(), top + cardH * 0.36f),
            size = Size(cardW - 28.dp.toPx(), 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
        )
        drawRoundRect(
            color = colors.muted2,
            topLeft = Offset(left + 14.dp.toPx(), top + cardH * 0.50f),
            size = Size(cardW - 28.dp.toPx(), 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
        )

        val fabCenter = Offset(left + cardW - 2.dp.toPx(), top + cardH - 2.dp.toPx())
        drawCircle(color = colors.surface, radius = 18.dp.toPx(), center = fabCenter)
        drawCircle(color = colors.primary, radius = 16.dp.toPx(), center = fabCenter)
        drawPlus(fabCenter, colors.onPrimaryWarm, arm = 6.dp, stroke = 2.5.dp)
    }
}

@Composable
fun SharedCostsIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationFrame(modifier) { size ->
        val w = size.width
        val h = size.height
        val receiptW = w * 0.34f
        val receiptH = h * 0.22f
        val left = (w - receiptW) / 2f
        val top = h * 0.14f
        val tooth = 6.dp.toPx()

        val clipW = 14.dp.toPx()
        val clipH = 8.dp.toPx()
        drawRoundRect(
            color = colors.onSurface,
            topLeft = Offset(left + 8.dp.toPx(), top - clipH + 2.dp.toPx()),
            size = Size(clipW, clipH),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        )

        val body = receiptPath(left, top, receiptW, receiptH, tooth)
        drawPath(body, colors.surface, style = Fill)
        drawPath(body, colors.onSurface, style = Stroke(width = 2.dp.toPx()))

        drawLine(
            colors.muted2,
            Offset(left + 16.dp.toPx(), top + 18.dp.toPx()),
            Offset(left + receiptW - 16.dp.toPx(), top + 18.dp.toPx()),
            2.dp.toPx(),
            StrokeCap.Round,
        )
        drawLine(
            colors.muted2,
            Offset(left + 16.dp.toPx(), top + 30.dp.toPx()),
            Offset(left + receiptW - 28.dp.toPx(), top + 30.dp.toPx()),
            2.dp.toPx(),
            StrokeCap.Round,
        )
        drawLabel(
            text = "$120",
            center = Offset(left + receiptW / 2f, top + receiptH * 0.58f),
            color = colors.primary,
            fontSizeSp = 24.dp.toPx(),
            bold = true,
            mono = true,
        )

        val hub = Offset(left + receiptW / 2f, top + receiptH + 4.dp.toPx())
        val personY = h * 0.68f
        val spacing = w / 4f
        repeat(3) { index ->
            val cx = spacing * (index + 1)
            val end = Offset(cx, personY - 22.dp.toPx())
            val dashed = Path().apply {
                moveTo(hub.x, hub.y)
                lineTo(end.x, end.y)
            }
            drawPath(
                color = colors.primary,
                path = dashed,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                ),
            )
            drawPersonIcon(Offset(cx, personY), radius = 18.dp, colors = colors)

            val pillW = 42.dp.toPx()
            val pillH = 20.dp.toPx()
            val pillLeft = cx - pillW / 2f
            val pillTop = personY + 24.dp.toPx()
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx()),
            )
            drawLabel(
                text = "$40",
                center = Offset(cx, pillTop + pillH / 2f),
                color = colors.onSurface,
                fontSizeSp = 12.dp.toPx(),
                mono = true,
            )
        }
    }
}

@Composable
fun EventBudgetIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationFrame(modifier) { size ->
        val w = size.width
        val h = size.height
        val poleX = w * 0.30f
        val poleTop = h * 0.18f
        val poleBottom = h * 0.62f

        drawLine(
            color = colors.onSurface,
            start = Offset(poleX, poleTop),
            end = Offset(poleX, poleBottom),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        val flag = Path().apply {
            moveTo(poleX, poleTop + 8.dp.toPx())
            lineTo(poleX + 40.dp.toPx(), poleTop + 18.dp.toPx())
            lineTo(poleX + 40.dp.toPx(), poleTop + 42.dp.toPx())
            lineTo(poleX, poleTop + 34.dp.toPx())
            close()
        }
        drawPath(flag, colors.primary)
        drawPath(flag, colors.onSurface, style = Stroke(width = 2.dp.toPx()))

        val calLeft = poleX - 18.dp.toPx()
        val calTop = poleBottom - 34.dp.toPx()
        val calW = 52.dp.toPx()
        val calH = 44.dp.toPx()
        drawRoundRect(
            color = colors.surface,
            topLeft = Offset(calLeft, calTop),
            size = Size(calW, calH),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 2.dp.toPx()),
        )
        drawRoundRect(
            color = colors.onSurface,
            topLeft = Offset(calLeft + 10.dp.toPx(), calTop - 6.dp.toPx()),
            size = Size(10.dp.toPx(), 10.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        )
        drawRoundRect(
            color = colors.onSurface,
            topLeft = Offset(calLeft + calW - 20.dp.toPx(), calTop - 6.dp.toPx()),
            size = Size(10.dp.toPx(), 10.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        )
        drawCircle(color = colors.primary, radius = 5.dp.toPx(), center = Offset(calLeft + calW / 2f, calTop + calH / 2f + 4.dp.toPx()))

        val donutCx = w * 0.68f
        val donutCy = h * 0.42f
        val donutR = 40.dp.toPx()
        val ring = 10.dp.toPx()
        drawCircle(
            color = colors.paperAlt,
            radius = donutR,
            center = Offset(donutCx, donutCy),
            style = Stroke(width = ring),
        )
        drawArc(
            color = colors.primary,
            startAngle = -90f,
            sweepAngle = 223f,
            useCenter = false,
            topLeft = Offset(donutCx - donutR, donutCy - donutR),
            size = Size(donutR * 2, donutR * 2),
            style = Stroke(width = ring, cap = StrokeCap.Round),
        )
        drawLabel(
            text = "62%",
            center = Offset(donutCx, donutCy - 4.dp.toPx()),
            color = colors.onSurface,
            fontSizeSp = 16.dp.toPx(),
            bold = true,
            mono = true,
        )
        drawLabel(
            text = "of budget",
            center = Offset(donutCx, donutCy + 12.dp.toPx()),
            color = colors.onSurfaceMuted,
            fontSizeSp = 10.dp.toPx(),
        )

        val plusCenter = Offset(donutCx, donutCy - donutR - 10.dp.toPx())
        drawPlus(plusCenter, colors.primary, arm = 6.dp, stroke = 2.5.dp)
    }
}

@Composable
fun JournalIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationFrame(modifier) { size ->
        val w = size.width
        val h = size.height
        val cardW = w * 0.54f
        val cardH = h * 0.24f
        val baseLeft = w * 0.24f
        val baseTop = h * 0.40f
        val radius = 12.dp.toPx()

        fun drawJournalCard(
            left: Float,
            top: Float,
            stroke: Float,
            showToday: Boolean,
            amount: String,
            withCheck: Boolean,
        ) {
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(left, top),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = stroke),
            )
            if (showToday) {
                drawLabel(
                    text = "TODAY",
                    center = Offset(left + 34.dp.toPx(), top + 16.dp.toPx()),
                    color = colors.onSurface,
                    fontSizeSp = 11.dp.toPx(),
                    bold = true,
                )
            }
            val iconCx = left + 28.dp.toPx()
            val iconCy = top + cardH * 0.58f
            if (withCheck) {
                drawCircle(color = colors.primary, radius = 10.dp.toPx(), center = Offset(iconCx, iconCy))
                drawCheckmark(Offset(iconCx, iconCy), colors.onPrimaryWarm, arm = 4.dp, stroke = 2.dp)
            } else {
                drawCircle(color = colors.primaryTint, radius = 8.dp.toPx(), center = Offset(iconCx, iconCy))
            }
            drawLine(
                colors.muted2,
                Offset(left + 46.dp.toPx(), iconCy),
                Offset(left + cardW - 56.dp.toPx(), iconCy),
                2.dp.toPx(),
                StrokeCap.Round,
            )
            drawLabel(
                text = amount,
                center = Offset(left + cardW - 28.dp.toPx(), iconCy),
                color = colors.primary,
                fontSizeSp = 16.dp.toPx(),
                bold = true,
                mono = true,
            )
        }

        rotate(degrees = -8f, pivot = Offset(baseLeft + cardW / 2f, baseTop + cardH / 2f)) {
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(baseLeft, baseTop),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }
        rotate(degrees = 6f, pivot = Offset(baseLeft + cardW / 2f + 8.dp.toPx(), baseTop + cardH / 2f - 12.dp.toPx())) {
            drawJournalCard(
                left = baseLeft + 10.dp.toPx(),
                top = baseTop - 18.dp.toPx(),
                stroke = 2.dp.toPx(),
                showToday = false,
                amount = "$12",
                withCheck = false,
            )
        }
        drawJournalCard(
            left = baseLeft + 18.dp.toPx(),
            top = baseTop - 36.dp.toPx(),
            stroke = 2.5.dp.toPx(),
            showToday = true,
            amount = "$42",
            withCheck = true,
        )
    }
}

@Composable
fun SplashBrandMark(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val elevation = ProExpenseTheme.elevation.fab.firstOrNull()

    Canvas(modifier = modifier) {
        val side = min(size.width, size.height)
        val corner = side * 0.25f
        val inset = side * 0.22f
        val handle = side * 0.07f

        if (elevation != null) {
            drawRoundRect(
                color = elevation.color,
                topLeft = Offset(4.dp.toPx(), 6.dp.toPx()),
                size = Size(side, side),
                cornerRadius = CornerRadius(corner, corner),
            )
        }

        drawRoundRect(
            color = colors.primary,
            size = Size(side, side),
            cornerRadius = CornerRadius(corner, corner),
        )

        val frame = UiRect(inset, inset, side - inset, side - inset)
        drawRoundRect(
            color = colors.onPrimaryWarm,
            topLeft = frame.topLeft,
            size = frame.size,
            cornerRadius = CornerRadius(side * 0.04f, side * 0.04f),
            style = Stroke(width = side * 0.045f),
        )

        val corners = listOf(
            Offset(frame.left, frame.top),
            Offset(frame.right, frame.top),
            Offset(frame.left, frame.bottom),
            Offset(frame.right, frame.bottom),
        )
        corners.forEach { point ->
            drawCircle(color = colors.onPrimaryWarm, radius = handle, center = point)
        }
    }
}
