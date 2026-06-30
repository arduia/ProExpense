package com.arduia.expense.domain

data class Category(
    val id: CategoryId,
    val name: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0,
) {
    init {
        require(name.isNotBlank()) { "Category name must not be blank" }
    }
}
