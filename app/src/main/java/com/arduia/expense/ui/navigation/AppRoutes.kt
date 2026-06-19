package com.arduia.expense.ui.navigation

object AppRoutes {
    const val REPORTS = "reports"
    const val CATEGORIES = "categories"
    const val CURRENCY = "currency"
    const val EXPORT = "export"
    const val CLEAR = "clear"
    const val JOURNAL_DETAIL = "journal_detail"
    const val EVENT_CREATE = "event_create"
    const val DEBT_TRACKER = "debt_tracker"
    const val DEBT_ADD = "debt_add"
    const val DEBT_DETAIL = "debt_detail"
    const val SHARED_INPUT = "shared_input"
    const val SHARED_SUMMARY = "shared_summary"
    const val SHARED_HISTORY = "shared_history"

    private const val EVENT_DETAIL_PREFIX = "event_detail:"

    fun eventDetail(title: String): String = "$EVENT_DETAIL_PREFIX$title"

    fun eventDetailTitle(route: String): String? =
        route.removePrefix(EVENT_DETAIL_PREFIX).takeIf { route.startsWith(EVENT_DETAIL_PREFIX) }
}

class AppNavigator(
    private val backStack: List<String>,
    private val onBackStackChange: (List<String>) -> Unit,
    private val onTabChange: (com.arduia.expense.ui.design.HomeNavTab) -> Unit,
) {
    val canPop: Boolean get() = backStack.isNotEmpty()

    fun push(route: String) {
        onBackStackChange(backStack + route)
    }

    fun pop() {
        if (backStack.isNotEmpty()) {
            onBackStackChange(backStack.dropLast(1))
        }
    }

    fun popToRoot() {
        onBackStackChange(emptyList())
    }

    fun replace(route: String) {
        onBackStackChange(listOf(route))
    }

    fun switchTab(tab: com.arduia.expense.ui.design.HomeNavTab) {
        popToRoot()
        onTabChange(tab)
    }
}
