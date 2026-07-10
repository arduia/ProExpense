package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SplitStrategy
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

    /**
     * Blocker guard: [parseStrategyJson]'s previous regex (`"shares":\{([^}]*)\}`) can't span
     * nested braces, so with 2+ participants (every real split — min is 2 people) it captured only
     * a truncated fragment of the first share entry, `CustomSplit`'s own shares.isNotEmpty() check
     * then threw on decode, and `observeAll()` swallows that exception per-row — silently dropping
     * every saved custom split from the history list.
     */
    @Test
    fun strategyJson_roundTripsCustomSplitWithMultipleParticipants() {
        val strategy =
            SplitStrategy.CustomSplit(
                shares =
                    mapOf(
                        ParticipantId("person 1-0-1000") to Money(Amount(7500), CurrencyCode("USD")),
                        ParticipantId("person 2-1-1000") to Money(Amount(4500), CurrencyCode("USD")),
                    ),
            )

        val json = strategy.toStrategyJson()
        val parsed = parseStrategyJson(json)

        assertEquals(strategy, parsed)
    }

    @Test
    fun strategyJson_equalSplit_roundTripsAsNull() {
        assertEquals(SplitStrategy.EqualSplit, parseStrategyJson(SplitStrategy.EqualSplit.toStrategyJson()))
    }
}
