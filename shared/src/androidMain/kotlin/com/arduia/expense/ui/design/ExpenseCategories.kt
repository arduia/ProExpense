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

fun expenseCategoryLabel(id: String): String =
    (defaultExpenseCategories + customExpenseCategories).firstOrNull { it.first == id }?.second ?: id
