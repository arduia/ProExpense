package com.arduia.expense.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ProExpenseEase = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)

@Immutable
data class ProExpenseMotion(
    val screenTransitionMs: Int = 280,
    val sheetTransitionMs: Int = 340,
    val toastDurationMs: Int = 2400,
    val fadeUpMs: Int = 200,
    val pulseHighlightMs: Int = 1800,
    val shakeMs: Int = 280,
    val tapScaleMs: Int = 80,
    val pressedScale: Float = 0.97f,
    val standardEasing: Easing = ProExpenseEase,
    val sheetMaxHeightFraction: Float = 0.78f,
    val sheetHandleWidth: Dp = 36.dp,
    val sheetHandleHeight: Dp = 4.dp,
) {
    fun screenTween(forward: Boolean = true) = tween<Float>(
        durationMillis = screenTransitionMs,
        easing = standardEasing,
    )

    fun sheetTween() = tween<Float>(
        durationMillis = sheetTransitionMs,
        easing = standardEasing,
    )

    fun tapTween() = tween<Float>(
        durationMillis = tapScaleMs,
        easing = standardEasing,
    )
}

val LocalProExpenseMotion = staticCompositionLocalOf { ProExpenseMotion() }
