package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CategoryTest {
    @Test
    fun `constructs with valid name`() {
        val category = Category(CategoryId("c1"), "Food")
        assertFalse(category.isCustom)
    }

    @Test
    fun `defaults type to expense`() {
        val category = Category(CategoryId("c1"), "Food")
        assertEquals(RecordType.EXPENSE, category.type)
    }

    @Test
    fun `constructs with explicit income type`() {
        val category = Category(CategoryId("salary"), "Salary", type = RecordType.INCOME)
        assertEquals(RecordType.INCOME, category.type)
    }

    @Test
    fun `rejects blank name`() {
        assertFailsWith<IllegalArgumentException> {
            Category(CategoryId("c1"), " ")
        }
    }
}
