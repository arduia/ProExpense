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

fun ProMotion.reducedEnter(): EnterTransition =
    fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.reducedExit(): ExitTransition =
    fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.forwardScreenEnter(reduceMotion: Boolean = false): EnterTransition =
    if (reduceMotion) {
        reducedEnter()
    } else {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
        ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.forwardScreenExit(reduceMotion: Boolean = false): ExitTransition =
    if (reduceMotion) {
        reducedExit()
    } else {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
        ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.backwardScreenEnter(reduceMotion: Boolean = false): EnterTransition =
    if (reduceMotion) {
        reducedEnter()
    } else {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
        ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.backwardScreenExit(reduceMotion: Boolean = false): ExitTransition =
    if (reduceMotion) {
        reducedExit()
    } else {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = screenDurationMillis, easing = standardEasing),
        ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.tabFadeEnter(reduceMotion: Boolean = false): EnterTransition =
    fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.tabFadeExit(reduceMotion: Boolean = false): ExitTransition =
    fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))

fun ProMotion.sheetEnter(reduceMotion: Boolean = false): EnterTransition =
    if (reduceMotion) {
        reducedEnter()
    } else {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = sheetDurationMillis, easing = standardEasing),
        ) + fadeIn(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.sheetExit(reduceMotion: Boolean = false): ExitTransition =
    if (reduceMotion) {
        reducedExit()
    } else {
        slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = sheetDurationMillis, easing = standardEasing),
        ) + fadeOut(tween(durationMillis = fadeDurationMillis, easing = standardEasing))
    }

fun ProMotion.stepTransition(
    fromIndex: Int,
    toIndex: Int,
    reduceMotion: Boolean = false,
): ContentTransform {
    return if (toIndex >= fromIndex) {
        forwardScreenEnter(reduceMotion) togetherWith forwardScreenExit(reduceMotion)
    } else {
        backwardScreenEnter(reduceMotion) togetherWith backwardScreenExit(reduceMotion)
    }
}
