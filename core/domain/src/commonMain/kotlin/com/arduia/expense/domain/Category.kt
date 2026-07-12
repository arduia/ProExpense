package com.arduia.expense.domain

data class Category(
    val id: CategoryId,
    val name: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0,
    /**
     * Catalogue key (e.g. "food", "coffee") selecting a custom category's icon + tint from the
     * shared category color/icon catalogue. Unused for default categories, whose own [id] already
     * is a catalogue key. Blank falls back to the catalogue's generic default.
     */
    val iconId: String = "",
    /**
     * Catalogue key (e.g. "shopping", "health") selecting a custom category's tint independently
     * of [iconId] — lets users differentiate custom categories by color without it implying an
     * icon change. Unused for default categories. Blank falls back to [iconId]'s own tint.
     */
    val colorId: String = "",
    val type: RecordType = RecordType.EXPENSE,
) {
    init {
        require(name.isNotBlank()) { "Category name must not be blank" }
    }
}
