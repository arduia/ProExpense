package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith

class EventTest {

    private val budget = Money(Amount(1000), CurrencyCode("USD"))

    @Test
    fun `constructs with valid date range`() {
        Event(
            id = EventId("e1"),
            name = "Trip",
            startEpochMillis = 0,
            endEpochMillis = 1000,
            budget = budget,
        )
    }

    @Test
    fun `allows zero-length window`() {
        Event(
            id = EventId("e1"),
            name = "Trip",
            startEpochMillis = 500,
            endEpochMillis = 500,
            budget = budget,
        )
    }

    @Test
    fun `rejects end before start`() {
        assertFailsWith<IllegalArgumentException> {
            Event(
                id = EventId("e1"),
                name = "Trip",
                startEpochMillis = 1000,
                endEpochMillis = 0,
                budget = budget,
            )
        }
    }

    @Test
    fun `rejects blank name`() {
        assertFailsWith<IllegalArgumentException> {
            Event(
                id = EventId("e1"),
                name = " ",
                startEpochMillis = 0,
                endEpochMillis = 1000,
                budget = budget,
            )
        }
    }
}
