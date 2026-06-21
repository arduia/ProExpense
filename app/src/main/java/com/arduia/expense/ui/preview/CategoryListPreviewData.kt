package com.arduia.expense.ui.preview

const val CATEGORY_NAME_MAX = 20

data class CategoryRowUi(
    val categoryId: String,
    val label: String,
)

data class CategoryListUiState(
    val defaults: List<CategoryRowUi>,
    val custom: List<CategoryRowUi>,
)

data class CategoryNewFormState(
    val name: String = "",
    val selectedIconId: String = "food",
    val iconOptions: List<String> = listOf("food", "coffee", "health", "pet"),
    val duplicate: Boolean = false,
) {
    val canAdd: Boolean
        get() = name.isNotBlank() && !duplicate
}

val previewCategoryList = CategoryListUiState(
    defaults = listOf(
        CategoryRowUi("food", "Food"),
        CategoryRowUi("transport", "Transport"),
        CategoryRowUi("shopping", "Shopping"),
        CategoryRowUi("bills", "Bills"),
        CategoryRowUi("health", "Health"),
        CategoryRowUi("entertainment", "Entertainment"),
    ),
    custom = listOf(
        CategoryRowUi("coffee", "Coffee runs"),
        CategoryRowUi("pet", "Pet care"),
    ),
)

val previewCategoryNewDuplicate = CategoryNewFormState(
    name = "Food",
    selectedIconId = "food",
    duplicate = true,
)
