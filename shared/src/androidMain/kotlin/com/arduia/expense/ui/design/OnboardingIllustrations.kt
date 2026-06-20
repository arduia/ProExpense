package com.arduia.expense.ui.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
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
        val s = size.minDimension / 280f
        fun pt(x: Float, y: Float) = Offset(x * s, y * s)

        drawCircle(color = colors.primaryTint.copy(alpha = 0.55f), radius = 118f * s, center = pt(140f, 118f))
        drawCircle(color = colors.primaryTint.copy(alpha = 0.35f), radius = 10f * s, center = pt(196f, 72f))

        val book = Path().apply {
            moveTo(82f * s, 58f * s)
            lineTo(198f * s, 58f * s)
            quadraticTo(208f * s, 58f * s, 208f * s, 68f * s)
            lineTo(208f * s, 178f * s)
            quadraticTo(208f * s, 188f * s, 198f * s, 188f * s)
            lineTo(82f * s, 188f * s)
            quadraticTo(72f * s, 188f * s, 72f * s, 178f * s)
            lineTo(72f * s, 68f * s)
            quadraticTo(72f * s, 58f * s, 82f * s, 58f * s)
            close()
        }
        drawPath(book, color = colors.surface)
        drawPath(book, color = colors.onSurface, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        drawLine(colors.lineStrong, pt(140f, 68f), pt(140f, 178f), strokeWidth = 1.6f * s)
        repeat(3) { i ->
            val y = 92f + i * 22f
            drawRoundRect(
                color = colors.lineStrong,
                topLeft = pt(88f, y),
                size = Size(42f * s, 4f * s),
                cornerRadius = CornerRadius(2f * s),
            )
        }
        drawCircle(color = colors.primary, radius = 18f * s, center = pt(168f, 132f))
        drawPath(
            Path().apply {
                moveTo(160f * s, 132f * s)
                lineTo(166f * s, 138f * s)
                lineTo(178f * s, 124f * s)
            },
            color = colors.onPrimaryWarm,
            style = Stroke(width = 2.4f * s, cap = StrokeCap.Round),
        )

        drawCircle(color = colors.primary, radius = 14f * s, center = pt(96f, 176f))
        drawLine(colors.onPrimaryWarm, pt(96f, 170f), pt(96f, 182f), strokeWidth = 2f * s, cap = StrokeCap.Round)
        drawLine(colors.onPrimaryWarm, pt(90f, 176f), pt(102f, 176f), strokeWidth = 2f * s, cap = StrokeCap.Round)

        drawCircle(color = colors.primary, radius = 12f * s, center = pt(214f, 148f))
        drawLine(colors.onPrimaryWarm, pt(214f, 142f), pt(214f, 154f), strokeWidth = 2f * s, cap = StrokeCap.Round)
        drawLine(colors.onPrimaryWarm, pt(208f, 148f), pt(220f, 148f), strokeWidth = 2f * s, cap = StrokeCap.Round)
    }
}

@Composable
private fun QuickLogIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val s = size.minDimension / 280f
        fun pt(x: Float, y: Float) = Offset(x * s, y * s)

        drawCircle(color = colors.primaryTint.copy(alpha = 0.55f), radius = 118f * s, center = pt(140f, 118f))
        repeat(3) { i ->
            drawRoundRect(
                color = colors.primary.copy(alpha = 0.35f),
                topLeft = pt(54f - i * 8f, 108f + i * 4f),
                size = Size((18f - i * 3f) * s, 4f * s),
                cornerRadius = CornerRadius(2f * s),
            )
        }

        val phone = Path().apply {
            moveTo(104f * s, 52f * s)
            lineTo(176f * s, 52f * s)
            quadraticTo(186f * s, 52f * s, 186f * s, 62f * s)
            lineTo(186f * s, 196f * s)
            quadraticTo(186f * s, 206f * s, 176f * s, 206f * s)
            lineTo(104f * s, 206f * s)
            quadraticTo(94f * s, 206f * s, 94f * s, 196f * s)
            lineTo(94f * s, 62f * s)
            quadraticTo(94f * s, 52f * s, 104f * s, 52f * s)
            close()
        }
        drawPath(phone, color = colors.surface)
        drawPath(phone, color = colors.onSurface, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        drawRoundRect(
            color = colors.primary,
            topLeft = pt(104f, 68f),
            size = Size(72f * s, 34f * s),
            cornerRadius = CornerRadius(0f),
        )
        drawRoundRect(
            color = colors.primaryTint,
            topLeft = pt(110f, 118f),
            size = Size(52f * s, 6f * s),
            cornerRadius = CornerRadius(3f * s),
        )
        drawRoundRect(
            color = colors.lineStrong,
            topLeft = pt(110f, 132f),
            size = Size(34f * s, 6f * s),
            cornerRadius = CornerRadius(3f * s),
        )
        drawCircle(color = colors.primary, radius = 16f * s, center = pt(176f, 188f))
        drawLine(colors.onPrimaryWarm, pt(176f, 180f), pt(176f, 196f), strokeWidth = 2.2f * s, cap = StrokeCap.Round)
        drawLine(colors.onPrimaryWarm, pt(168f, 188f), pt(184f, 188f), strokeWidth = 2.2f * s, cap = StrokeCap.Round)
    }
}

@Composable
private fun SharedCostsIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val s = size.minDimension / 280f
        fun pt(x: Float, y: Float) = Offset(x * s, y * s)

        drawCircle(color = colors.primaryTint.copy(alpha = 0.55f), radius = 118f * s, center = pt(140f, 118f))

        val receipt = Path().apply {
            moveTo(108f * s, 62f * s)
            lineTo(172f * s, 62f * s)
            lineTo(172f * s, 132f * s)
            lineTo(164f * s, 126f * s)
            lineTo(156f * s, 132f * s)
            lineTo(148f * s, 126f * s)
            lineTo(140f * s, 132f * s)
            lineTo(132f * s, 126f * s)
            lineTo(124f * s, 132f * s)
            lineTo(116f * s, 126f * s)
            lineTo(108f * s, 132f * s)
            close()
        }
        drawPath(receipt, color = colors.surface)
        drawPath(receipt, color = colors.onSurface, style = Stroke(width = 2f * s, cap = StrokeCap.Round))

        listOf(88f, 140f, 192f).forEach { x ->
            drawLine(
                color = colors.primary.copy(alpha = 0.5f),
                start = pt(140f, 132f),
                end = pt(x, 168f),
                strokeWidth = 1.6f * s,
                cap = StrokeCap.Round,
            )
            drawCircle(color = colors.primary, radius = 14f * s, center = pt(x, 182f))
            drawCircle(color = colors.surface, radius = 8f * s, center = pt(x, 182f))
            drawRoundRect(
                color = colors.surface,
                topLeft = pt(x - 18f, 202f),
                size = Size(36f * s, 16f * s),
                cornerRadius = CornerRadius(8f * s),
            )
            drawRoundRect(
                color = colors.onSurface,
                topLeft = pt(x - 18f, 202f),
                size = Size(36f * s, 16f * s),
                cornerRadius = CornerRadius(8f * s),
                style = Stroke(width = 1.2f * s),
            )
        }
    }
}

@Composable
private fun EventBudgetIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val s = size.minDimension / 280f
        fun pt(x: Float, y: Float) = Offset(x * s, y * s)

        drawCircle(color = colors.primaryTint.copy(alpha = 0.55f), radius = 118f * s, center = pt(140f, 118f))
        drawLine(colors.onSurface, pt(118f, 188f), pt(118f, 84f), strokeWidth = 2.2f * s, cap = StrokeCap.Round)
        val flag = Path().apply {
            moveTo(118f * s, 84f * s)
            lineTo(154f * s, 98f * s)
            lineTo(118f * s, 112f * s)
            close()
        }
        drawPath(flag, color = colors.primary)
        drawRoundRect(
            color = colors.surface,
            topLeft = pt(96f, 176f),
            size = Size(44f * s, 34f * s),
            cornerRadius = CornerRadius(8f * s),
        )
        drawRoundRect(
            color = colors.onSurface,
            topLeft = pt(96f, 176f),
            size = Size(44f * s, 34f * s),
            cornerRadius = CornerRadius(8f * s),
            style = Stroke(width = 2f * s),
        )
        drawCircle(color = colors.primary, radius = 4f * s, center = pt(118f, 193f))

        drawCircle(
            color = colors.lineStrong,
            radius = 42f * s,
            center = pt(188f, 132f),
            style = Stroke(width = 8f * s),
        )
        drawArc(
            color = colors.primary,
            startAngle = -90f,
            sweepAngle = 220f,
            useCenter = false,
            topLeft = pt(146f, 90f),
            size = Size(84f * s, 84f * s),
            style = Stroke(width = 8f * s, cap = StrokeCap.Round),
        )
        drawCircle(color = colors.primary, radius = 10f * s, center = pt(214f, 88f))
        drawLine(colors.onPrimaryWarm, pt(214f, 82f), pt(214f, 94f), strokeWidth = 2f * s, cap = StrokeCap.Round)
        drawLine(colors.onPrimaryWarm, pt(208f, 88f), pt(220f, 88f), strokeWidth = 2f * s, cap = StrokeCap.Round)
    }
}

@Composable
private fun JournalIllustration(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    Canvas(modifier = modifier) {
        val s = size.minDimension / 280f
        fun pt(x: Float, y: Float) = Offset(x * s, y * s)

        drawCircle(color = colors.primaryTint.copy(alpha = 0.55f), radius = 118f * s, center = pt(140f, 118f))

        listOf(0f to 0.92f, 1f to 1f, 2f to 0.88f).forEach { (index, scale) ->
            val offsetY = index * 18f
            val cardWidth = 150f * scale
            val left = 140f - cardWidth / 2f
            drawRoundRect(
                color = colors.surface,
                topLeft = pt(left, 88f + offsetY),
                size = Size(cardWidth * s, 72f * s),
                cornerRadius = CornerRadius(14f * s),
            )
            drawRoundRect(
                color = colors.onSurface,
                topLeft = pt(left, 88f + offsetY),
                size = Size(cardWidth * s, 72f * s),
                cornerRadius = CornerRadius(14f * s),
                style = Stroke(width = 1.6f * s),
            )
            if (index == 2f) {
                drawCircle(color = colors.primary, radius = 10f * s, center = pt(left + 22f, 108f + offsetY))
                drawRoundRect(
                    color = colors.lineStrong,
                    topLeft = pt(left + 40f, 102f + offsetY),
                    size = Size(48f * s, 4f * s),
                    cornerRadius = CornerRadius(2f * s),
                )
                drawRoundRect(
                    color = colors.lineStrong,
                    topLeft = pt(left + 40f, 112f + offsetY),
                    size = Size(32f * s, 4f * s),
                    cornerRadius = CornerRadius(2f * s),
                )
            }
        }
    }
}

@Preview(widthDp = 280, heightDp = 280, showBackground = true)
@Composable
private fun OnboardingIllustrationsPreview() {
    ProExpenseTheme {
        WelcomeIllustration()
    }
}
