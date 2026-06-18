package com.arduia.expense.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

// Motion — DESIGN-SYSTEM.md §9. Brisk and eased, never bouncy.
private val ProExpenseEase = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)

@Immutable
data class ProExpenseMotion(
    val screenTransitionMs: Int = 280,
    val sheetTransitionMs: Int = 340,
    val toastDurationMs: Int = 2400,
    val fadeUpMs: Int = 200,
    val tapScaleMs: Int = 80,
    val pressedScale: Float = 0.97f,
    val disabledOpacity: Float = 0.4f,
    val standardEasing: Easing = ProExpenseEase,
) {
    fun screenTween() = tween<Float>(durationMillis = screenTransitionMs, easing = standardEasing)

    fun tapTween() = tween<Float>(durationMillis = tapScaleMs, easing = standardEasing)
}

val LocalProExpenseMotion = staticCompositionLocalOf { ProExpenseMotion() }
