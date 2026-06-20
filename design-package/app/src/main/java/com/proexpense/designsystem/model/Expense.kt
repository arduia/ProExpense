package com.proexpense.designsystem.model

/* ─────────────────────────────────────────────────────────────────────────
 * Expense + tag models and sample data (mirrors flow-01-main.jsx seed).
 * ───────────────────────────────────────────────────────────────────────── */

sealed interface ExpenseTag {
    val label: String
    data class Event(val id: String, override val label: String) : ExpenseTag
    data class Debt(val id: String, override val label: String) : ExpenseTag
}

data class Expense(
    val id: String,
    val amount: Double,
    val category: Category,
    val note: String = "",
    val date: String = "Today",
    val time: String = "12:30 PM",
    val tag: ExpenseTag? = null,
)

/** Home contextual-header persona — the three "all states" of the header card. */
enum class HomePersona { Casual, Budget, Event }

data class EventInfo(val name: String, val remaining: Double, val budget: Double, val days: Int)

object SampleData {
    val expenses = listOf(
        Expense("e1", 12.40, Category.Food, "Lunch with M.", "Today", "12:30 PM"),
        Expense("e2", 3.50, Category.Transport, "", "Today", "09:15 AM"),
        Expense("e3", 5.00, Category.Coffee, "Oat latte", "Today", "08:40 AM"),
        Expense("e4", 18.00, Category.Entertainment, "Movie · Dune", "Yesterday", "08:10 PM", ExpenseTag.Event("bali", "Bali Trip")),
        Expense("e5", 42.00, Category.Food, "Groceries", "Yesterday", "05:30 PM"),
    )

    const val monthBudget = 500.0
    val activeEvent = EventInfo("Bali Trip", remaining = 1240.0, budget = 2000.0, days = 14)
}

fun currencyFmt(n: Double): String {
    if (n.isNaN()) return "$0"
    val whole = n.toLong()
    return if (n % 1.0 == 0.0) "$%,d".format(whole) else "$%,.2f".format(n)
}
