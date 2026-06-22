package com.arduia.expense.storage.db

import com.arduia.expense.domain.Category

/**
 * Built-in categories seeded on first launch. Stable string ids so they survive export/import
 * and so a default selection (Food, per the PRD/D9) can be referenced without a DB lookup.
 */
object DefaultCategories {
    const val FOOD_ID = "cat_food"

    val all: List<Category> = listOf(
        Category(id = FOOD_ID, name = "Food", isCustom = false),
        Category(id = "cat_transport", name = "Transport", isCustom = false),
        Category(id = "cat_shopping", name = "Shopping", isCustom = false),
        Category(id = "cat_bills", name = "Bills", isCustom = false),
        Category(id = "cat_entertainment", name = "Entertainment", isCustom = false),
        Category(id = "cat_health", name = "Health", isCustom = false),
        Category(id = "cat_other", name = "Other", isCustom = false),
    )
}
