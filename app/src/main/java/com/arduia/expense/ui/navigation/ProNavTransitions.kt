package com.arduia.expense.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProMotion
import com.arduia.expense.ui.theme.backwardScreenEnter
import com.arduia.expense.ui.theme.backwardScreenExit
import com.arduia.expense.ui.theme.forwardScreenEnter
import com.arduia.expense.ui.theme.forwardScreenExit
import com.arduia.expense.ui.theme.stepTransition
import com.arduia.expense.ui.theme.tabFadeEnter
import com.arduia.expense.ui.theme.tabFadeExit
import androidx.compose.animation.togetherWith

data class AppNavState(
    val route: String?,
    val tab: HomeNavTab,
    val stackSize: Int,
) {
    val displayKey: String get() = route ?: "tab:${tab.name}"
}

enum class NavTransitionKind {
    Forward,
    Backward,
    TabFade,
}

fun resolveNavTransitionKind(
    from: AppNavState,
    to: AppNavState,
): NavTransitionKind {
    return when {
        to.stackSize > from.stackSize -> NavTransitionKind.Forward
        to.stackSize < from.stackSize -> NavTransitionKind.Backward
        from.displayKey.startsWith("tab:") &&
            to.displayKey.startsWith("tab:") &&
            from.displayKey != to.displayKey -> NavTransitionKind.TabFade
        from.displayKey != to.displayKey -> NavTransitionKind.Forward
        else -> NavTransitionKind.Forward
    }
}

fun AnimatedContentTransitionScope<AppNavState>.appNavTransition(motion: ProMotion): ContentTransform {
    return when (resolveNavTransitionKind(initialState, targetState)) {
        NavTransitionKind.Forward ->
            motion.forwardScreenEnter() togetherWith motion.forwardScreenExit()
        NavTransitionKind.Backward ->
            motion.backwardScreenEnter() togetherWith motion.backwardScreenExit()
        NavTransitionKind.TabFade ->
            motion.tabFadeEnter() togetherWith motion.tabFadeExit()
    }
}

fun stepTransition(
    motion: ProMotion,
    fromIndex: Int,
    toIndex: Int,
): ContentTransform = motion.stepTransition(fromIndex, toIndex)
