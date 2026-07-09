package com.arduia.expense.domain

/**
 * Built-in categories seeded on first launch. Rows must exist so Add Expense category chips can
 * read from storage rather than hardcoding labels in the UI.
 */
val DEFAULT_CATEGORIES: List<Category> =
    listOf(
        Category(CategoryId("food"), "Food"),
        Category(CategoryId("transport"), "Transport"),
        Category(CategoryId("shopping"), "Shopping"),
        Category(CategoryId("bills"), "Bills"),
        Category(CategoryId("health"), "Health"),
        Category(CategoryId("entertainment"), "Entertainment"),
    )

/**
 * Sentinel category id a [FinanceRecord] is reassigned to when its real category is deleted.
 * Never a selectable [Category] row in storage — excluded from the logging picker by construction,
 * but still a valid `categoryId` value shown in Journal/Reports.
 */
const val UNCATEGORIZED_CATEGORY_ID: String = "uncategorized"
