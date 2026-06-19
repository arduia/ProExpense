package com.arduia.expense.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

fun ProMotion.forwardScreenEnter(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
    ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.forwardScreenExit(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
    ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.backwardScreenEnter(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
    ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.backwardScreenExit(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
    ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.tabFadeEnter(): EnterTransition =
    fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.tabFadeExit(): ExitTransition =
    fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.sheetEnter(): EnterTransition =
    slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = sheetDurationMillis, easing = standardEasing),
    ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.sheetExit(): ExitTransition =
    slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = sheetDurationMillis, easing = standardEasing),
    ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.stepTransition(
    fromIndex: Int,
    toIndex: Int,
): ContentTransform {
    return if (toIndex >= fromIndex) {
        forwardScreenEnter() togetherWith forwardScreenExit()
    } else {
        backwardScreenEnter() togetherWith backwardScreenExit()
    }
}
