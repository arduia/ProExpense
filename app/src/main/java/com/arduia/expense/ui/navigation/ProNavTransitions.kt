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
    TabForward,
    TabBackward,
}

private fun HomeNavTab.slideOrdinal(): Int = when (this) {
    HomeNavTab.Home -> 0
    HomeNavTab.Budget -> 1
    HomeNavTab.Add -> 2
    HomeNavTab.Journal -> 3
    HomeNavTab.More -> 4
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
            from.displayKey != to.displayKey -> {
            if (to.tab.slideOrdinal() >= from.tab.slideOrdinal()) {
                NavTransitionKind.TabForward
            } else {
                NavTransitionKind.TabBackward
            }
        }
        from.displayKey != to.displayKey -> NavTransitionKind.Forward
        else -> NavTransitionKind.Forward
    }
}

fun AnimatedContentTransitionScope<AppNavState>.appNavTransition(
    motion: ProMotion,
    reduceMotion: Boolean = false,
): ContentTransform {
    return when (resolveNavTransitionKind(initialState, targetState)) {
        NavTransitionKind.Forward ->
            motion.forwardScreenEnter(reduceMotion) togetherWith motion.forwardScreenExit(reduceMotion)
        NavTransitionKind.Backward ->
            motion.backwardScreenEnter(reduceMotion) togetherWith motion.backwardScreenExit(reduceMotion)
        NavTransitionKind.TabForward ->
            motion.forwardScreenEnter(reduceMotion) togetherWith motion.forwardScreenExit(reduceMotion)
        NavTransitionKind.TabBackward ->
            motion.backwardScreenEnter(reduceMotion) togetherWith motion.backwardScreenExit(reduceMotion)
    }
}

fun stepTransition(
    motion: ProMotion,
    fromIndex: Int,
    toIndex: Int,
    reduceMotion: Boolean = false,
): ContentTransform = motion.stepTransition(fromIndex, toIndex, reduceMotion)
