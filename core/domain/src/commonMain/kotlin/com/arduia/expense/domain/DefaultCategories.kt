package com.arduia.expense.domain

/**
 * Built-in categories seeded on first launch. Rows must exist so Add Expense category chips can
 * read from storage rather than hardcoding labels in the UI.
 */
val DEFAULT_CATEGORIES: List<Category> = listOf(
    Category(CategoryId("food"), "Food"),
    Category(CategoryId("transport"), "Transport"),
    Category(CategoryId("shopping"), "Shopping"),
    Category(CategoryId("bills"), "Bills"),
    Category(CategoryId("health"), "Health"),
    Category(CategoryId("entertainment"), "Entertainment"),
)
