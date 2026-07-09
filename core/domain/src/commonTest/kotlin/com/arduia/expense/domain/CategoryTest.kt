package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CategoryTest {
    @Test
    fun `constructs with valid name`() {
        val category = Category(CategoryId("c1"), "Food")
        assertFalse(category.isCustom)
    }

    @Test
    fun `rejects blank name`() {
        assertFailsWith<IllegalArgumentException> {
            Category(CategoryId("c1"), " ")
        }
    }
}
