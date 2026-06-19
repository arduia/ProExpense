package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

private val illustrationSize = 280.dp
private val haloSize = 220.dp

@Composable
private fun OnboardingIllustrationHalo(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors

    Box(
        modifier = modifier.size(illustrationSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(haloSize)) {
            drawCircle(color = colors.primaryTint.copy(alpha = 0.55f))
        }
        content()
    }
}

@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationHalo(modifier) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val notebookW = size.width * 0.72f
            val notebookH = size.height * 0.62f
            val left = (size.width - notebookW) / 2f
            val top = size.height * 0.18f
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(left, top),
                size = Size(notebookW, notebookH),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                style = Stroke(width = 2.5.dp.toPx()),
            )
            val lineY1 = top + notebookH * 0.28f
            val lineY2 = top + notebookH * 0.48f
            val lineXStart = left + notebookW * 0.12f
            val lineXEnd = left + notebookW * 0.55f
            drawLine(colors.muted2, Offset(lineXStart, lineY1), Offset(lineXEnd, lineY1), 2.dp.toPx())
            drawLine(colors.muted2, Offset(lineXStart, lineY2), Offset(lineXEnd, lineY2), 2.dp.toPx())
            val checkCx = left + notebookW * 0.72f
            val checkCy = top + notebookH * 0.35f
            drawCircle(colors.primary, radius = 18.dp.toPx(), center = Offset(checkCx, checkCy))
            val checkPath = Path().apply {
                moveTo(checkCx - 8.dp.toPx(), checkCy)
                lineTo(checkCx - 2.dp.toPx(), checkCy + 7.dp.toPx())
                lineTo(checkCx + 9.dp.toPx(), checkCy - 8.dp.toPx())
            }
            drawPath(checkPath, colors.onPrimaryWarm, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(
                color = colors.primary,
                radius = 10.dp.toPx(),
                center = Offset(left + notebookW + 4.dp.toPx(), top + notebookH * 0.55f),
            )
            drawLine(
                colors.onPrimaryWarm,
                Offset(left + notebookW - 2.dp.toPx(), top + notebookH * 0.55f),
                Offset(left + notebookW + 10.dp.toPx(), top + notebookH * 0.55f),
                2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                colors.onPrimaryWarm,
                Offset(left + notebookW + 4.dp.toPx(), top + notebookH * 0.48f),
                Offset(left + notebookW + 4.dp.toPx(), top + notebookH * 0.62f),
                2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun QuickLogIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationHalo(modifier) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val cardW = size.width * 0.55f
            val cardH = size.height * 0.72f
            val left = size.width * 0.28f
            val top = size.height * 0.12f
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(left, top),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(width = 2.5.dp.toPx()),
            )
            drawRoundRect(
                color = colors.primary,
                topLeft = Offset(left, top),
                size = Size(cardW, cardH * 0.22f),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
            )
            drawLine(
                colors.primarySoft,
                Offset(left + 12.dp.toPx(), top + cardH * 0.42f),
                Offset(left + cardW - 12.dp.toPx(), top + cardH * 0.42f),
                3.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                colors.muted2,
                Offset(left + 12.dp.toPx(), top + cardH * 0.58f),
                Offset(left + cardW - 12.dp.toPx(), top + cardH * 0.58f),
                3.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = colors.primary,
                radius = 16.dp.toPx(),
                center = Offset(left + cardW + 6.dp.toPx(), top + cardH - 8.dp.toPx()),
            )
            drawLine(
                colors.onPrimaryWarm,
                Offset(left + cardW - 4.dp.toPx(), top + cardH - 8.dp.toPx()),
                Offset(left + cardW + 16.dp.toPx(), top + cardH - 8.dp.toPx()),
                2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                colors.onPrimaryWarm,
                Offset(left + cardW + 6.dp.toPx(), top + cardH - 16.dp.toPx()),
                Offset(left + cardW + 6.dp.toPx(), top + cardH),
                2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            for (i in 0..2) {
                drawLine(
                    colors.primarySoft,
                    Offset(size.width * 0.08f, top + cardH * 0.35f + i * 8.dp.toPx()),
                    Offset(size.width * 0.2f, top + cardH * 0.35f + i * 8.dp.toPx()),
                    2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
fun SharedCostsIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationHalo(modifier) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val receiptW = size.width * 0.38f
            val receiptH = size.height * 0.28f
            val receiptLeft = (size.width - receiptW) / 2f
            val receiptTop = size.height * 0.08f
            val receiptPath = Path().apply {
                addRect(Rect(receiptLeft, receiptTop, receiptLeft + receiptW, receiptTop + receiptH))
            }
            drawPath(receiptPath, colors.surface, style = Stroke(2.dp.toPx()))
            drawPath(receiptPath, colors.surface, style = Fill)
            val personY = size.height * 0.62f
            val personSpacing = size.width / 4f
            repeat(3) { index ->
                val cx = personSpacing * (index + 1)
                drawLine(
                    colors.primary,
                    Offset(size.width / 2f, receiptTop + receiptH),
                    Offset(cx, personY - 18.dp.toPx()),
                    2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(color = colors.primary, radius = 16.dp.toPx(), center = Offset(cx, personY))
                drawCircle(
                    color = colors.surface,
                    radius = 7.dp.toPx(),
                    center = Offset(cx, personY - 4.dp.toPx()),
                )
                drawRoundRect(
                    color = colors.surface,
                    topLeft = Offset(cx - 18.dp.toPx(), personY + 22.dp.toPx()),
                    size = Size(36.dp.toPx(), 16.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    style = Stroke(1.5.dp.toPx()),
                )
            }
        }
    }
}

@Composable
fun EventBudgetIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationHalo(modifier) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val flagX = size.width * 0.22f
            val flagBaseY = size.height * 0.72f
            drawLine(colors.onSurface, Offset(flagX, flagBaseY), Offset(flagX, size.height * 0.18f), 2.5.dp.toPx())
            val flagPath = Path().apply {
                moveTo(flagX, size.height * 0.22f)
                lineTo(flagX + 36.dp.toPx(), size.height * 0.3f)
                lineTo(flagX, size.height * 0.38f)
                close()
            }
            drawPath(flagPath, colors.primary)
            val calLeft = flagX + 20.dp.toPx()
            val calTop = size.height * 0.48f
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(calLeft, calTop),
                size = Size(44.dp.toPx(), 40.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = Stroke(2.dp.toPx()),
            )
            drawCircle(colors.primary, radius = 4.dp.toPx(), center = Offset(calLeft + 22.dp.toPx(), calTop + 22.dp.toPx()))
            val donutCx = size.width * 0.68f
            val donutCy = size.height * 0.48f
            val donutR = 38.dp.toPx()
            drawCircle(color = colors.paperAlt, radius = donutR, center = Offset(donutCx, donutCy), style = Stroke(10.dp.toPx()))
            drawArc(
                color = colors.primary,
                startAngle = -90f,
                sweepAngle = 223f,
                useCenter = false,
                topLeft = Offset(donutCx - donutR, donutCy - donutR),
                size = Size(donutR * 2, donutR * 2),
                style = Stroke(10.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}

@Composable
fun JournalIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    OnboardingIllustrationHalo(modifier) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val cardW = size.width * 0.62f
            val cardH = size.height * 0.38f
            val baseLeft = size.width * 0.2f
            val baseTop = size.height * 0.34f
            rotate(degrees = -8f, pivot = Offset(baseLeft + cardW / 2f, baseTop + cardH / 2f)) {
                drawRoundRect(
                    color = colors.surface,
                    topLeft = Offset(baseLeft, baseTop),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(1.5.dp.toPx()),
                )
            }
            rotate(degrees = 6f, pivot = Offset(baseLeft + cardW / 2f + 8.dp.toPx(), baseTop + cardH / 2f - 12.dp.toPx())) {
                drawRoundRect(
                    color = colors.surface,
                    topLeft = Offset(baseLeft + 10.dp.toPx(), baseTop - 18.dp.toPx()),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(2.dp.toPx()),
                )
            }
            drawRoundRect(
                color = colors.surface,
                topLeft = Offset(baseLeft + 18.dp.toPx(), baseTop - 36.dp.toPx()),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                style = Stroke(2.5.dp.toPx()),
            )
            drawLine(
                colors.muted2,
                Offset(baseLeft + 30.dp.toPx(), baseTop - 18.dp.toPx()),
                Offset(baseLeft + cardW - 4.dp.toPx(), baseTop - 18.dp.toPx()),
                2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = colors.primary,
                radius = 8.dp.toPx(),
                center = Offset(baseLeft + 34.dp.toPx(), baseTop - 26.dp.toPx()),
            )
        }
    }
}
