package com.arduia.expense.storage.db

import com.arduia.expense.domain.Category

/**
 * Built-in categories seeded on first launch. IDs are kept in lock-step with the design-system
 * catalogue (`defaultExpenseCategories` in ui/design) so a stored categoryId resolves to the
 * right icon/tint without a lookup, and a default selection (Food, per the PRD/D9) can be
 * referenced by a stable id.
 */
object DefaultCategories {
    const val FOOD_ID = "food"

    val all: List<Category> = listOf(
        Category(id = FOOD_ID, name = "Food", isCustom = false),
        Category(id = "transport", name = "Transport", isCustom = false),
        Category(id = "shopping", name = "Shopping", isCustom = false),
        Category(id = "bills", name = "Bills", isCustom = false),
        Category(id = "health", name = "Health", isCustom = false),
        Category(id = "entertainment", name = "Entertainment", isCustom = false),
    )
}
