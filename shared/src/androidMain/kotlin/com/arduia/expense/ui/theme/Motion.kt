package com.arduia.expense.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class ProMotion(
    val screenDurationMillis: Int,
    val sheetDurationMillis: Int,
    val fadeDurationMillis: Int,
    val tapDurationMillis: Int,
    val shakeDurationMillis: Int,
    val rowPulseDurationMillis: Int,
    val toastDurationMillis: Int,
    val standardEasing: Easing,
    val pressedScale: Float,
    val pressedOpacity: Float,
    val disabledOpacity: Float,
    val keypadDisabledOpacity: Float,
)

val LocalProMotion = staticCompositionLocalOf { ProDefaultMotion }

val ProStandardEasing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)

val ProDefaultMotion = ProMotion(
    screenDurationMillis = 280,
    sheetDurationMillis = 340,
    fadeDurationMillis = 200,
    tapDurationMillis = 80,
    shakeDurationMillis = 280,
    rowPulseDurationMillis = 1800,
    toastDurationMillis = 2400,
    standardEasing = ProStandardEasing,
    pressedScale = 0.97f,
    pressedOpacity = 0.92f,
    disabledOpacity = 0.4f,
    keypadDisabledOpacity = 0.55f,
)
