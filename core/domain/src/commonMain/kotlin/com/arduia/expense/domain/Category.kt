package com.arduia.expense.domain

data class Category(
    val id: String,
    val name: String,
    val isCustom: Boolean = false,
)
