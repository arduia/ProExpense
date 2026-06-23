package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class DebtTest {

    private val money = Money(Amount(500), CurrencyCode("USD"))

    @Test
    fun `constructs with valid person name`() {
        val debt = Debt(
            id = DebtId("d1"),
            personName = "John",
            money = money,
            direction = DebtDirection.I_OWE,
        )
        assertFalse(debt.isSettled)
    }

    @Test
    fun `rejects blank person name`() {
        assertFailsWith<IllegalArgumentException> {
            Debt(
                id = DebtId("d1"),
                personName = " ",
                money = money,
                direction = DebtDirection.I_OWE,
            )
        }
    }
}
