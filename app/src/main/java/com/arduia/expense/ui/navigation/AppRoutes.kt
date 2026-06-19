package com.arduia.expense.ui.navigation

object AppRoutes {
    const val REPORTS = "reports"
    const val CATEGORIES = "categories"
    const val CURRENCY = "currency"
    const val EXPORT = "export"
    const val CLEAR = "clear"
    const val JOURNAL_DETAIL_PREFIX = "journal_detail:"

    fun journalDetail(recordId: String): String = "$JOURNAL_DETAIL_PREFIX$recordId"

    fun journalDetailId(route: String): String? =
        route.removePrefix(JOURNAL_DETAIL_PREFIX).takeIf { route.startsWith(JOURNAL_DETAIL_PREFIX) }
    const val EVENT_CREATE = "event_create"
    const val DEBT_TRACKER = "debt_tracker"
    const val DEBT_ADD_PREFIX = "debt_add:"

    fun debtAdd(isLent: Boolean): String = "$DEBT_ADD_PREFIX${if (isLent) "lent" else "owe"}"

    fun debtAddIsLent(route: String): Boolean? =
        when (route) {
            "${DEBT_ADD_PREFIX}lent" -> true
            "${DEBT_ADD_PREFIX}owe" -> false
            else -> null
        }

    const val DEBT_DETAIL_PREFIX = "debt_detail:"

    fun debtDetail(debtId: String): String = "$DEBT_DETAIL_PREFIX$debtId"

    fun debtDetailId(route: String): String? =
        route.removePrefix(DEBT_DETAIL_PREFIX).takeIf { route.startsWith(DEBT_DETAIL_PREFIX) }
    const val SHARED_INPUT = "shared_input"
    const val SHARED_SUMMARY_PREFIX = "shared_summary:"

    fun sharedSummary(sharedCostId: String): String = "$SHARED_SUMMARY_PREFIX$sharedCostId"

    fun sharedSummaryId(route: String): String? =
        route.removePrefix(SHARED_SUMMARY_PREFIX).takeIf { route.startsWith(SHARED_SUMMARY_PREFIX) }

    const val SHARED_HISTORY = "shared_history"

    private const val EVENT_DETAIL_PREFIX = "event_detail:"

    fun eventDetail(eventId: String): String = "$EVENT_DETAIL_PREFIX$eventId"

    fun eventDetailId(route: String): String? =
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
