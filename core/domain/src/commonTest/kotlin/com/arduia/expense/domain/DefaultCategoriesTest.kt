package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultCategoriesTest {
    @Test
    fun seedsSixExpenseCategories() {
        val expenseCategories = DEFAULT_CATEGORIES.filter { it.type == RecordType.EXPENSE }
        assertEquals(6, expenseCategories.size)
    }

    @Test
    fun seedsIncomeAsSalaryAndGift() {
        val incomeCategories = DEFAULT_CATEGORIES.filter { it.type == RecordType.INCOME }
        assertEquals(listOf("income", "salary", "gift"), incomeCategories.map { it.id.value })
    }
}
