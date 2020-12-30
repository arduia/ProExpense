package com.arduia.expense.backup

data class MainCategoryField(
    val id: Int,
    val name: String
) {
    companion object {
        const val FILED_ID = "ID"
        const val FIELD_NAME = "NAME"
    }
}
