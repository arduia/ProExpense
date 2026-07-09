package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IdsTest {
    @Test
    fun `constructs with non-blank value`() {
        assertEquals("r1", RecordId("r1").value)
        assertEquals("c1", CategoryId("c1").value)
        assertEquals("p1", ParticipantId("p1").value)
        assertEquals("d1", DebtId("d1").value)
        assertEquals("e1", EventId("e1").value)
        assertEquals("s1", SharedCostId("s1").value)
    }

    @Test
    fun `equal string values are structurally equal`() {
        assertEquals(RecordId("r1"), RecordId("r1"))
        assertEquals(CategoryId("c1"), CategoryId("c1"))
    }

    @Test
    fun `rejects blank value`() {
        assertFailsWith<IllegalArgumentException> { RecordId("") }
        assertFailsWith<IllegalArgumentException> { CategoryId(" ") }
        assertFailsWith<IllegalArgumentException> { ParticipantId("") }
        assertFailsWith<IllegalArgumentException> { DebtId("") }
        assertFailsWith<IllegalArgumentException> { EventId("") }
        assertFailsWith<IllegalArgumentException> { SharedCostId("") }
    }
}
