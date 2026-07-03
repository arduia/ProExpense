package com.arduia.expense.ui.design

val defaultExpenseCategories = listOf(
    "food" to "Food",
    "transport" to "Transport",
    "shopping" to "Shopping",
    "bills" to "Bills",
    "health" to "Health",
    "entertainment" to "Entertainment",
)

val customExpenseCategories = listOf(
    "coffee" to "Coffee runs",
    "pet" to "Pet care",
)

private const val UNCATEGORIZED_ID = "uncategorized"

fun expenseCategoryLabel(id: String): String = when {
    id == UNCATEGORIZED_ID -> "Uncategorized"
    else -> (defaultExpenseCategories + customExpenseCategories).firstOrNull { it.first == id }?.second ?: id
}
