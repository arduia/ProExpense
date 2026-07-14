package com.arduia.expense.feature.categories.ui.preview

import com.arduia.expense.domain.RecordType

const val CATEGORY_NAME_MAX = 20

data class CategoryRowUi(
    val categoryId: String,
    val label: String,
    val iconId: String = "",
    val colorId: String = "",
    val type: RecordType = RecordType.EXPENSE,
    val isCustom: Boolean = false,
)

data class CategoryListUiState(
    val categories: List<CategoryRowUi>,
)

data class CategoryNewFormState(
    val name: String = "",
    val selectedIconId: String = "food",
    val iconOptions: List<String> = categoryIconOptions,
    val selectedColorId: String = "other",
    val duplicate: Boolean = false,
    val type: RecordType = RecordType.EXPENSE,
) {
    val canAdd: Boolean
        get() = name.isNotBlank() && !duplicate
}

val categoryColorOptions =
    listOf("food", "transport", "shopping", "bills", "health", "entertainment", "coffee", "pet", "other")

val categoryIconOptions =
    listOf(
        "food",
        "transport",
        "shopping",
        "bills",
        "health",
        "entertainment",
        "coffee",
        "pet",
        "home",
        "education",
        "travel",
        "fitness",
        "subscription",
        "gift",
        "insurance",
        "utilities",
        "family",
        "salary",
        "savings",
        "beauty",
    )

val previewCategoryList =
    CategoryListUiState(
        categories =
            listOf(
                CategoryRowUi("food", "Food"),
                CategoryRowUi("transport", "Transport"),
                CategoryRowUi("coffee", "Coffee runs", isCustom = true),
                CategoryRowUi("shopping", "Shopping"),
                CategoryRowUi("bills", "Bills"),
                CategoryRowUi("pet", "Pet care", isCustom = true),
                CategoryRowUi("health", "Health"),
                CategoryRowUi("entertainment", "Entertainment"),
                CategoryRowUi("income", "Income", type = RecordType.INCOME),
                CategoryRowUi("salary", "Salary", type = RecordType.INCOME),
                CategoryRowUi("gift", "Gift", type = RecordType.INCOME),
            ),
    )

val previewCategoryNewDuplicate =
    CategoryNewFormState(
        name = "Food",
        selectedIconId = "food",
        duplicate = true,
    )
