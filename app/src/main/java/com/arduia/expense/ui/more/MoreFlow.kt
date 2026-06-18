package com.arduia.expense.ui.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

/** Screens that make up the self-contained More / Settings flow. */
private enum class MoreScreen { Hub, Currency, Export, ClearData }

/**
 * Self-contained More / Settings flow: the hub plus its currency, export, and clear-data
 * sub-screens. Internal navigation is held in local state and animated with the standard screen
 * slide; sub-screens return to the hub via their back affordance.
 *
 * Sub-screens that are not yet built (Reports, Debt Tracker, Shared Costs, Categories, Monthly
 * budget, PIN, Language, Theme) are surfaced through [onNavigateOut] so a future host can route
 * them once those flows land.
 */
@Composable
fun MoreFlowScreen(
    modifier: Modifier = Modifier,
    initialCurrencyCode: String = "USD",
    onNavigateOut: (MoreDestination) -> Unit = {},
    onTabSelect: (BottomTab) -> Unit = {},
    onAdd: () -> Unit = {},
    onExport: () -> Unit = {},
    onClearData: (Set<ClearScope>) -> Unit = {},
) {
    var current by remember { mutableStateOf(MoreScreen.Hub) }
    var currencyCode by remember { mutableStateOf(initialCurrencyCode) }
    var clearSelection by remember { mutableStateOf(setOf(ClearScope.Expenses)) }

    val motion = ProExpenseTheme.motion

    AnimatedContent(
        targetState = current,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            val forward = targetState != MoreScreen.Hub
            val spec = tween<IntOffset>(motion.screenTransitionMs, easing = motion.standardEasing)
            val fadeSpec = tween<Float>(motion.screenTransitionMs, easing = motion.standardEasing)
            if (forward) {
                (slideInHorizontally(spec) { it } + fadeIn(fadeSpec)) togetherWith
                    (slideOutHorizontally(spec) { -it / 4 } + fadeOut(fadeSpec))
            } else {
                (slideInHorizontally(spec) { -it / 4 } + fadeIn(fadeSpec)) togetherWith
                    (slideOutHorizontally(spec) { it } + fadeOut(fadeSpec))
            }
        },
        label = "moreFlow",
    ) { screen ->
        when (screen) {
            MoreScreen.Hub -> MoreHubScreen(
                state = MoreHubState(currencyCode = currencyCode),
                onNavigate = { destination ->
                    when (destination) {
                        MoreDestination.Currency -> current = MoreScreen.Currency
                        MoreDestination.Export -> current = MoreScreen.Export
                        MoreDestination.ClearData -> current = MoreScreen.ClearData
                        else -> onNavigateOut(destination)
                    }
                },
                onTabSelect = onTabSelect,
                onAdd = onAdd,
            )

            MoreScreen.Currency -> MoreCurrencyScreen(
                selectedCode = currencyCode,
                onCurrencySelect = { currencyCode = it.code },
                onBack = { current = MoreScreen.Hub },
            )

            MoreScreen.Export -> MoreExportScreen(
                onBack = { current = MoreScreen.Hub },
                onExport = onExport,
            )

            MoreScreen.ClearData -> MoreClearDataScreen(
                selected = clearSelection,
                onToggle = { scope ->
                    clearSelection = if (scope in clearSelection) {
                        clearSelection - scope
                    } else {
                        clearSelection + scope
                    }
                },
                onConfirmClear = { scopes ->
                    onClearData(scopes)
                    current = MoreScreen.Hub
                },
                onBack = { current = MoreScreen.Hub },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun MoreFlowScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            MoreFlowScreen()
        }
    }
}
