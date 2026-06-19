package com.arduia.expense.ui.navigation

import com.arduia.expense.ui.design.HomeNavTab
import org.junit.Assert.assertEquals
import org.junit.Test

class ProNavTransitionsTest {

    private fun state(
        route: String? = null,
        tab: HomeNavTab = HomeNavTab.Home,
        stackSize: Int = if (route == null) 0 else 1,
    ) = AppNavState(route = route, tab = tab, stackSize = stackSize)

    @Test
    fun push_from_tab_is_forward() {
        val kind = resolveNavTransitionKind(
            from = state(tab = HomeNavTab.Home, stackSize = 0),
            to = state(route = AppRoutes.REPORTS, stackSize = 1),
        )
        assertEquals(NavTransitionKind.Forward, kind)
    }

    @Test
    fun pop_to_tab_is_backward() {
        val kind = resolveNavTransitionKind(
            from = state(route = AppRoutes.REPORTS, stackSize = 1),
            to = state(tab = HomeNavTab.Home, stackSize = 0),
        )
        assertEquals(NavTransitionKind.Backward, kind)
    }

    @Test
    fun tab_switch_forward_is_slide_forward() {
        val kind = resolveNavTransitionKind(
            from = state(tab = HomeNavTab.Home, stackSize = 0),
            to = state(tab = HomeNavTab.Budget, stackSize = 0),
        )
        assertEquals(NavTransitionKind.TabForward, kind)
    }

    @Test
    fun tab_switch_backward_is_slide_backward() {
        val kind = resolveNavTransitionKind(
            from = state(tab = HomeNavTab.Journal, stackSize = 0),
            to = state(tab = HomeNavTab.Home, stackSize = 0),
        )
        assertEquals(NavTransitionKind.TabBackward, kind)
    }

    @Test
    fun nested_push_is_forward() {
        val kind = resolveNavTransitionKind(
            from = state(route = AppRoutes.DEBT_TRACKER, stackSize = 1),
            to = state(route = AppRoutes.debtAdd(isLent = true), stackSize = 2),
        )
        assertEquals(NavTransitionKind.Forward, kind)
    }

    @Test
    fun replace_route_is_forward() {
        val kind = resolveNavTransitionKind(
            from = state(route = AppRoutes.sharedSummary("preview"), stackSize = 2),
            to = state(route = AppRoutes.SHARED_HISTORY, stackSize = 2),
        )
        assertEquals(NavTransitionKind.Forward, kind)
    }
}
