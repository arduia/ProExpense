package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedCostMapperTest {
    @Test
    fun participantsJson_roundTripsANameContainingQuotesAndNewlines() {
        val participants =
            listOf(
                Participant(id = ParticipantId("p1"), name = "Alice \"the Great\"\nJones"),
            )

        val json = participants.toParticipantsJson()
        val parsed = parseParticipantsJson(json)

        assertEquals(participants, parsed)
    }

    @Test
    fun participantsJson_emptyList_roundTrips() {
        assertEquals(emptyList(), parseParticipantsJson(emptyList<Participant>().toParticipantsJson()))
    }
}
