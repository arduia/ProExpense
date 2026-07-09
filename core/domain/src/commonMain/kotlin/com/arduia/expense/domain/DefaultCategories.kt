package com.arduia.expense.domain

/**
 * Built-in categories seeded on first launch. Rows must exist so Add Expense category chips can
 * read from storage rather than hardcoding labels in the UI.
 */
val DEFAULT_CATEGORIES: List<Category> =
    listOf(
        Category(CategoryId("food"), "Food", type = RecordType.EXPENSE),
        Category(CategoryId("transport"), "Transport", type = RecordType.EXPENSE),
        Category(CategoryId("shopping"), "Shopping", type = RecordType.EXPENSE),
        Category(CategoryId("bills"), "Bills", type = RecordType.EXPENSE),
        Category(CategoryId("health"), "Health", type = RecordType.EXPENSE),
        Category(CategoryId("entertainment"), "Entertainment", type = RecordType.EXPENSE),
        Category(CategoryId("income"), "Income", type = RecordType.INCOME),
        Category(CategoryId("salary"), "Salary", type = RecordType.INCOME),
        Category(CategoryId("gift"), "Gift", type = RecordType.INCOME),
    )

/**
 * Sentinel category id a [FinanceRecord] is reassigned to when its real category is deleted.
 * Never a selectable [Category] row in storage — excluded from the logging picker by construction,
 * but still a valid `categoryId` value shown in Journal/Reports.
 */
const val UNCATEGORIZED_CATEGORY_ID: String = "uncategorized"
