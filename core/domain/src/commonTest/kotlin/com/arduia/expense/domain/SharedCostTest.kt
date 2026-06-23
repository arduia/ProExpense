package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SharedCostTest {

    private val usd = CurrencyCode("USD")
    private val p1 = Participant(ParticipantId("p1"), "Alice")
    private val p2 = Participant(ParticipantId("p2"), "Bob")

    @Test
    fun `constructs with equal split and shares matches resolve`() {
        val sharedCost = SharedCost(
            id = SharedCostId("sc1"),
            title = "Dinner",
            total = Money(Amount(100), usd),
            participants = listOf(p1, p2),
            splitStrategy = SplitStrategy.EqualSplit,
            recordedAtEpochMillis = 0,
        )

        assertEquals(
            SplitStrategy.resolve(SplitStrategy.EqualSplit, listOf(p1, p2), sharedCost.total),
            sharedCost.shares(),
        )
    }

    @Test
    fun `rejects duplicate participant ids`() {
        assertFailsWith<IllegalArgumentException> {
            SharedCost(
                id = SharedCostId("sc1"),
                title = "Dinner",
                total = Money(Amount(100), usd),
                participants = listOf(p1, p1),
                splitStrategy = SplitStrategy.EqualSplit,
                recordedAtEpochMillis = 0,
            )
        }
    }

    @Test
    fun `rejects empty participants`() {
        assertFailsWith<IllegalArgumentException> {
            SharedCost(
                id = SharedCostId("sc1"),
                title = "Dinner",
                total = Money(Amount(100), usd),
                participants = emptyList(),
                splitStrategy = SplitStrategy.EqualSplit,
                recordedAtEpochMillis = 0,
            )
        }
    }

    @Test
    fun `rejects blank title`() {
        assertFailsWith<IllegalArgumentException> {
            SharedCost(
                id = SharedCostId("sc1"),
                title = " ",
                total = Money(Amount(100), usd),
                participants = listOf(p1, p2),
                splitStrategy = SplitStrategy.EqualSplit,
                recordedAtEpochMillis = 0,
            )
        }
    }
}
