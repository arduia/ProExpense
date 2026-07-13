package com.arduia.expense.ui.design

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.arduia.expense.ui.theme.ProColors

// Drawing primitives shared by OnboardingIllustrations.kt — split out to keep each file's
// function count under the detekt TooManyFunctions threshold. Illustrations are ported 1:1
// from the Claude Design "Pro Expense - Finance Tracker" project (onboarding-illustrations.jsx),
// whose SVGs use a 240x200 viewBox — kept here for scale math.
internal const val ILLO_WIDTH = 240f
internal const val ILLO_HEIGHT = 200f

internal fun DrawScope.illoTransform(): Pair<Float, Offset> {
    val s = size.width / ILLO_WIDTH
    return s to Offset(0f, (size.height - ILLO_HEIGHT * s) / 2f)
}

internal fun DrawScope.drawIlloText(
    text: String,
    position: Offset,
    fontSizePx: Float,
    color: Color,
    align: Paint.Align = Paint.Align.CENTER,
) {
    val paint =
        Paint().apply {
            isAntiAlias = true
            textSize = fontSizePx
            textAlign = align
            typeface = Typeface.DEFAULT_BOLD
            this.color = color.toArgb()
        }
    drawContext.canvas.nativeCanvas.drawText(text, position.x, position.y, paint)
}

internal fun DrawScope.drawIlloBackdrop(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)
    drawOval(color = colors.primaryTint.copy(alpha = 0.35f), topLeft = pt(28f, 30f), size = Size(184f * s, 156f * s))
}

internal fun DrawScope.drawNotebook(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    drawRoundRect(
        color = colors.surface,
        topLeft = pt(62f, 58f),
        size = Size(116f * s, 92f * s),
        cornerRadius = CornerRadius(10f * s),
    )
    drawRoundRect(
        color = colors.onSurface,
        topLeft = pt(62f, 58f),
        size = Size(116f * s, 92f * s),
        cornerRadius = CornerRadius(10f * s),
        style = Stroke(width = 3f * s),
    )
    drawLine(colors.onSurface, pt(120f, 58f), pt(120f, 150f), strokeWidth = 3f * s)
    drawLine(colors.muted2, pt(74f, 80f), pt(108f, 80f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    drawLine(colors.muted2, pt(74f, 94f), pt(108f, 94f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    drawLine(colors.muted2, pt(74f, 108f), pt(100f, 108f), strokeWidth = 3f * s, cap = StrokeCap.Round)

    drawCircle(color = colors.primary, radius = 16f * s, center = pt(150f, 92f))
    drawPath(
        Path().apply {
            moveTo(pt(143f, 92f).x, pt(143f, 92f).y)
            lineTo(pt(148f, 97f).x, pt(148f, 97f).y)
            lineTo(pt(157f, 87f).x, pt(157f, 87f).y)
        },
        color = colors.surface,
        style = Stroke(width = 3.4f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
    drawLine(colors.muted2, pt(132f, 118f), pt(166f, 118f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    drawLine(colors.muted2, pt(132f, 130f), pt(156f, 130f), strokeWidth = 3f * s, cap = StrokeCap.Round)
}

internal fun DrawScope.drawWelcomeCoin(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    drawCircle(color = colors.primarySoft, radius = 18f * s, center = pt(58f, 138f))
    drawCircle(color = colors.onSurface, radius = 18f * s, center = pt(58f, 138f), style = Stroke(width = 3f * s))

    val dollar =
        Path().apply {
            moveTo(pt(58f, 129f).x, pt(58f, 129f).y)
            lineTo(pt(58f, 147f).x, pt(58f, 147f).y)
            moveTo(pt(53f, 134f).x, pt(53f, 134f).y)
            lineTo(pt(61f, 134f).x, pt(61f, 134f).y)
            arcTo(
                rect = Rect(center = pt(61f, 137f), radius = 3f * s),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false,
            )
            lineTo(pt(55f, 140f).x, pt(55f, 140f).y)
            arcTo(
                rect = Rect(center = pt(55f, 143f), radius = 3f * s),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false,
            )
            lineTo(pt(63f, 146f).x, pt(63f, 146f).y)
        }
    drawPath(
        dollar,
        color = colors.surface,
        style = Stroke(width = 2.6f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

internal fun DrawScope.drawPhone(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    drawRoundRect(
        color = colors.surface,
        topLeft = pt(84f, 46f),
        size = Size(86f * s, 124f * s),
        cornerRadius = CornerRadius(16f * s),
    )
    drawRoundRect(
        color = colors.onSurface,
        topLeft = pt(84f, 46f),
        size = Size(86f * s, 124f * s),
        cornerRadius = CornerRadius(16f * s),
        style = Stroke(width = 3f * s),
    )
    drawRoundRect(
        color = colors.primary,
        topLeft = pt(84f, 46f),
        size = Size(86f * s, 40f * s),
        cornerRadius = CornerRadius(16f * s),
    )
    drawRect(color = colors.primary, topLeft = pt(84f, 70f), size = Size(86f * s, 16f * s))
    drawCircle(color = colors.primary, radius = 22f * s, center = pt(150f, 150f))
    drawCircle(color = colors.surface, radius = 22f * s, center = pt(150f, 150f), style = Stroke(width = 3f * s))

    drawRoundRect(
        color = colors.primaryTint,
        topLeft = pt(96f, 98f),
        size = Size(62f * s, 10f * s),
        cornerRadius = CornerRadius(5f * s),
    )
    drawRoundRect(
        color = colors.muted.copy(alpha = 0.4f),
        topLeft = pt(96f, 114f),
        size = Size(44f * s, 10f * s),
        cornerRadius = CornerRadius(5f * s),
    )
}

internal fun DrawScope.drawReceipt(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    val receipt =
        Path().apply {
            moveTo(pt(96f, 44f).x, pt(96f, 44f).y)
            lineTo(pt(144f, 44f).x, pt(144f, 44f).y)
            quadraticTo(pt(148f, 44f).x, pt(148f, 44f).y, pt(148f, 48f).x, pt(148f, 48f).y)
            lineTo(pt(148f, 110f).x, pt(148f, 110f).y)
            var x = 148f
            var y = 110f
            listOf(-5f, 5f, -5f, 5f, -5f, 5f, -5f, 5f).forEach { dy ->
                x -= 7f
                y += dy
                lineTo(pt(x, y).x, pt(x, y).y)
            }
            lineTo(pt(92f, 48f).x, pt(92f, 48f).y)
            quadraticTo(pt(92f, 44f).x, pt(92f, 44f).y, pt(96f, 44f).x, pt(96f, 44f).y)
            close()
        }
    drawPath(receipt, color = colors.surface)
    drawPath(receipt, color = colors.onSurface, style = Stroke(width = 3f * s, join = StrokeJoin.Round))
    drawLine(colors.muted2, pt(104f, 60f), pt(140f, 60f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    drawLine(colors.muted2, pt(104f, 72f), pt(132f, 72f), strokeWidth = 3f * s, cap = StrokeCap.Round)
}

internal fun DrawScope.drawJournalStack(
    s: Float,
    o: Offset,
    colors: ProColors,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    drawRoundRect(
        color = colors.surface.copy(alpha = 0.7f),
        topLeft = pt(74f, 50f),
        size = Size(108f * s, 44f * s),
        cornerRadius = CornerRadius(10f * s),
    )
    drawRoundRect(
        color = colors.muted2.copy(alpha = 0.7f),
        topLeft = pt(74f, 50f),
        size = Size(108f * s, 44f * s),
        cornerRadius = CornerRadius(10f * s),
        style = Stroke(width = 3f * s),
    )

    drawRoundRect(
        color = colors.surface,
        topLeft = pt(66f, 74f),
        size = Size(108f * s, 50f * s),
        cornerRadius = CornerRadius(10f * s),
    )
    drawRoundRect(
        color = colors.onSurface,
        topLeft = pt(66f, 74f),
        size = Size(108f * s, 50f * s),
        cornerRadius = CornerRadius(10f * s),
        style = Stroke(width = 3f * s),
    )
    drawCircle(color = colors.primaryTint, radius = 7f * s, center = pt(84f, 92f))
    drawLine(colors.muted2, pt(98f, 88f), pt(140f, 88f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    drawIlloText("$12", pt(160f, 93f), 11f * s, colors.primary, Paint.Align.RIGHT)
    drawLine(colors.muted2, pt(98f, 104f), pt(124f, 104f), strokeWidth = 3f * s, cap = StrokeCap.Round)
}

internal fun DrawScope.drawPerson(
    s: Float,
    o: Offset,
    colors: ProColors,
    px: Float,
    avatar: Color,
) {
    fun pt(
        x: Float,
        y: Float,
    ) = Offset(x * s + o.x, y * s + o.y)

    drawLine(
        color = colors.primary,
        start = pt(120f, 118f),
        end = pt(px, 150f),
        strokeWidth = 2.6f * s,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s)),
    )
    drawCircle(color = avatar, radius = 12f * s, center = pt(px, 160f))
    drawCircle(color = colors.onSurface, radius = 12f * s, center = pt(px, 160f), style = Stroke(width = 3f * s))
    drawCircle(color = colors.surface, radius = 4f * s, center = pt(px, 156f))
    drawArc(
        color = colors.surface,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = pt(px - 7f, 161f),
        size = Size(14f * s, 14f * s),
    )
    drawRoundRect(
        color = colors.surface,
        topLeft = pt(px - 16f, 176f),
        size = Size(32f * s, 13f * s),
        cornerRadius = CornerRadius(6f * s),
    )
    drawRoundRect(
        color = colors.onSurface,
        topLeft = pt(px - 16f, 176f),
        size = Size(32f * s, 13f * s),
        cornerRadius = CornerRadius(6f * s),
        style = Stroke(width = 2f * s),
    )
    drawIlloText("$40", pt(px, 185f), 8f * s, colors.onSurface)
}
