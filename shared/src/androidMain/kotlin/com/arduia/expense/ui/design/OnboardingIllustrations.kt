package com.arduia.expense.ui.design

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class OnboardingIllustration {
    Welcome,
    QuickLog,
    SharedCosts,
    EventBudget,
    Journal,
}

@Composable
fun OnboardingIllustration(
    type: OnboardingIllustration,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions
    when (type) {
        OnboardingIllustration.Welcome -> WelcomeIllustration(modifier.size(dimens.onboardingIllustrationSize))
        OnboardingIllustration.QuickLog -> QuickLogIllustration(modifier.size(dimens.onboardingIllustrationSize))
        OnboardingIllustration.SharedCosts -> SharedCostsIllustration(modifier.size(dimens.onboardingIllustrationSize))
        OnboardingIllustration.EventBudget -> EventBudgetIllustration(modifier.size(dimens.onboardingIllustrationSize))
        OnboardingIllustration.Journal -> JournalIllustration(modifier.size(dimens.onboardingIllustrationSize))
    }
}

@Composable
private fun WelcomeIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val (s, o) = illoTransform()

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * s + o.x, y * s + o.y)

        drawIlloBackdrop(s, o, colors)
        drawCircle(color = colors.primaryTint, radius = 9f * s, center = pt(186f, 44f))
        drawCircle(color = colors.primaryTint, radius = 6f * s, center = pt(44f, 150f))

        drawNotebook(s, o, colors)
        drawWelcomeCoin(s, o, colors)

        drawLine(colors.primary, pt(180f, 120f), pt(180f, 132f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawLine(colors.primary, pt(174f, 126f), pt(186f, 126f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    }
}

@Composable
private fun QuickLogIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val (s, o) = illoTransform()

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * s + o.x, y * s + o.y)

        drawIlloBackdrop(s, o, colors)
        drawLine(colors.primaryTint, pt(30f, 78f), pt(56f, 78f), strokeWidth = 5f * s, cap = StrokeCap.Round)
        drawLine(colors.primaryTint, pt(24f, 100f), pt(52f, 100f), strokeWidth = 5f * s, cap = StrokeCap.Round)
        drawLine(colors.primaryTint, pt(32f, 122f), pt(56f, 122f), strokeWidth = 5f * s, cap = StrokeCap.Round)

        drawPhone(s, o, colors)
        drawLine(colors.onPrimary, pt(150f, 140f), pt(150f, 160f), strokeWidth = 3.6f * s, cap = StrokeCap.Round)
        drawLine(colors.onPrimary, pt(140f, 150f), pt(160f, 150f), strokeWidth = 3.6f * s, cap = StrokeCap.Round)

        val bolt =
            Path().apply {
                moveTo(pt(64f, 150f).x, pt(64f, 150f).y)
                lineTo(pt(54f, 166f).x, pt(54f, 166f).y)
                lineTo(pt(62f, 166f).x, pt(62f, 166f).y)
                lineTo(pt(58f, 180f).x, pt(58f, 180f).y)
                lineTo(pt(72f, 160f).x, pt(72f, 160f).y)
                lineTo(pt(63f, 160f).x, pt(63f, 160f).y)
                close()
            }
        drawPath(bolt, color = colors.primarySoft)
        drawPath(bolt, color = colors.onSurface, style = Stroke(width = 2.4f * s, join = StrokeJoin.Round))
    }
}

@Composable
private fun SharedCostsIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val (s, o) = illoTransform()

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * s + o.x, y * s + o.y)

        drawIlloBackdrop(s, o, colors)
        drawReceipt(s, o, colors)
        drawIlloText("$120", pt(120f, 98f), 16f * s, colors.primary)

        listOf(60f to colors.primary, 120f to colors.primarySoft, 180f to colors.primaryDeep).forEach { (x, avatar) ->
            drawPerson(s, o, colors, x, avatar)
        }
    }
}

@Composable
private fun EventBudgetIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val (s, o) = illoTransform()

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * s + o.x, y * s + o.y)

        drawIlloBackdrop(s, o, colors)
        drawLine(colors.onSurface, pt(74f, 60f), pt(74f, 150f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        val flag =
            Path().apply {
                moveTo(pt(74f, 60f).x, pt(74f, 60f).y)
                lineTo(pt(108f, 60f).x, pt(108f, 60f).y)
                lineTo(pt(100f, 69f).x, pt(100f, 69f).y)
                lineTo(pt(108f, 78f).x, pt(108f, 78f).y)
                lineTo(pt(74f, 78f).x, pt(74f, 78f).y)
                close()
            }
        drawPath(flag, color = colors.primary)

        drawArc(
            color = colors.muted2,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = pt(128f, 74f),
            size = Size(60f * s, 60f * s),
            style = Stroke(width = 9f * s),
        )
        drawArc(
            color = colors.primary,
            startAngle = -90f,
            sweepAngle = 360f * 0.62f,
            useCenter = false,
            topLeft = pt(128f, 74f),
            size = Size(60f * s, 60f * s),
            style = Stroke(width = 9f * s, cap = StrokeCap.Round),
        )
        drawIlloText("62%", pt(158f, 102f), 14f * s, colors.onSurface)
        drawIlloText("of budget", pt(158f, 116f), 8f * s, colors.onSurfaceMuted)

        drawRoundRect(
            color = colors.surface,
            topLeft = pt(58f, 158f),
            size = Size(40f * s, 32f * s),
            cornerRadius = CornerRadius(6f * s),
        )
        drawRoundRect(
            color = colors.onSurface,
            topLeft = pt(58f, 158f),
            size = Size(40f * s, 32f * s),
            cornerRadius = CornerRadius(6f * s),
            style = Stroke(width = 3f * s),
        )
        drawLine(colors.onSurface, pt(58f, 168f), pt(98f, 168f), strokeWidth = 3f * s)
        drawLine(colors.onSurface, pt(68f, 154f), pt(68f, 162f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawLine(colors.onSurface, pt(88f, 154f), pt(88f, 162f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawCircle(color = colors.primary, radius = 4f * s, center = pt(78f, 179f))

        drawLine(colors.primarySoft, pt(150f, 52f), pt(150f, 62f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawLine(colors.primarySoft, pt(145f, 57f), pt(155f, 57f), strokeWidth = 3f * s, cap = StrokeCap.Round)
    }
}

@Composable
private fun JournalIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val (s, o) = illoTransform()

        fun pt(
            x: Float,
            y: Float,
        ) = Offset(x * s + o.x, y * s + o.y)

        drawIlloBackdrop(s, o, colors)
        drawJournalStack(s, o, colors)

        drawRoundRect(
            color = colors.surface,
            topLeft = pt(58f, 104f),
            size = Size(116f * s, 56f * s),
            cornerRadius = CornerRadius(12f * s),
        )
        drawRoundRect(
            color = colors.onSurface,
            topLeft = pt(58f, 104f),
            size = Size(116f * s, 56f * s),
            cornerRadius = CornerRadius(12f * s),
            style = Stroke(width = 3f * s),
        )
        drawIlloText("TODAY", pt(70f, 122f), 9f * s, colors.onSurface, Paint.Align.LEFT)
        drawCircle(color = colors.primary, radius = 8f * s, center = pt(78f, 140f))
        drawPath(
            Path().apply {
                moveTo(pt(74f, 140f).x, pt(74f, 140f).y)
                lineTo(pt(77f, 143f).x, pt(77f, 143f).y)
                lineTo(pt(83f, 134f).x, pt(83f, 134f).y)
            },
            color = colors.surface,
            style = Stroke(width = 2.4f * s, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawLine(colors.muted2, pt(94f, 136f), pt(138f, 136f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawLine(colors.muted2, pt(94f, 146f), pt(120f, 146f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        drawIlloText("$42", pt(162f, 142f), 12f * s, colors.primary, Paint.Align.RIGHT)
    }
}

@Preview(widthDp = 280, heightDp = 280, showBackground = true)
@Composable
private fun OnboardingIllustrationsPreview() {
    ProExpenseTheme {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            WelcomeIllustration(Modifier.size(280.dp))
            QuickLogIllustration(Modifier.size(280.dp))
            SharedCostsIllustration(Modifier.size(280.dp))
            EventBudgetIllustration(Modifier.size(280.dp))
            JournalIllustration(Modifier.size(280.dp))
        }
    }
}
