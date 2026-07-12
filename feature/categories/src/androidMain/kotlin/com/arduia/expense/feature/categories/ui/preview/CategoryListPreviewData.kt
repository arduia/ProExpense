package com.arduia.expense.feature.categories.ui.preview

import com.arduia.expense.domain.RecordType

const val CATEGORY_NAME_MAX = 20

data class CategoryRowUi(
    val categoryId: String,
    val label: String,
    val iconId: String = "",
    val colorId: String = "",
    val type: RecordType = RecordType.EXPENSE,
)

data class CategoryListUiState(
    val defaults: List<CategoryRowUi>,
    val custom: List<CategoryRowUi>,
)

data class CategoryNewFormState(
    val name: String = "",
    val selectedIconId: String = "food",
    val iconOptions: List<String> = listOf("food", "coffee", "health", "pet"),
    val selectedColorId: String = "other",
    val duplicate: Boolean = false,
    val type: RecordType = RecordType.EXPENSE,
) {
    val canAdd: Boolean
        get() = name.isNotBlank() && !duplicate
}

val categoryColorOptions =
    listOf("food", "transport", "shopping", "bills", "health", "entertainment", "coffee", "pet", "other")

val previewCategoryList =
    CategoryListUiState(
        defaults =
            listOf(
                CategoryRowUi("food", "Food"),
                CategoryRowUi("transport", "Transport"),
                CategoryRowUi("shopping", "Shopping"),
                CategoryRowUi("bills", "Bills"),
                CategoryRowUi("health", "Health"),
                CategoryRowUi("entertainment", "Entertainment"),
                CategoryRowUi("income", "Income", type = RecordType.INCOME),
                CategoryRowUi("salary", "Salary", type = RecordType.INCOME),
                CategoryRowUi("gift", "Gift", type = RecordType.INCOME),
            ),
        custom =
            listOf(
                CategoryRowUi("coffee", "Coffee runs"),
                CategoryRowUi("pet", "Pet care"),
            ),
    )

val previewCategoryNewDuplicate =
    CategoryNewFormState(
        name = "Food",
        selectedIconId = "food",
        duplicate = true,
    )
