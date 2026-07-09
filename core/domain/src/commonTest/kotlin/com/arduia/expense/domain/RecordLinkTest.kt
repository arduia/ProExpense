package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class RecordLinkTest {
    @Test
    fun `constructs each link kind`() {
        val none: RecordLink = RecordLink.None
        val toEvent: RecordLink = RecordLink.ToEvent(EventId("e1"))
        val toDebt: RecordLink = RecordLink.ToDebt(DebtId("d1"))
        val toSharedCost: RecordLink = RecordLink.ToSharedCost(SharedCostId("s1"))

        assertEquals(EventId("e1"), (toEvent as RecordLink.ToEvent).eventId)
        assertEquals(DebtId("d1"), (toDebt as RecordLink.ToDebt).debtId)
        assertEquals(SharedCostId("s1"), (toSharedCost as RecordLink.ToSharedCost).sharedCostId)
        assertEquals(RecordLink.None, none)
    }

    @Test
    fun `is exhaustively matchable without an else branch`() {
        fun describe(link: RecordLink): String =
            when (link) {
                RecordLink.None -> "none"
                is RecordLink.ToEvent -> "event:${link.eventId.value}"
                is RecordLink.ToDebt -> "debt:${link.debtId.value}"
                is RecordLink.ToSharedCost -> "sharedCost:${link.sharedCostId.value}"
            }

        assertEquals("event:e1", describe(RecordLink.ToEvent(EventId("e1"))))
    }
}
