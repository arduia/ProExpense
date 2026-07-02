package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SplitStrategyTest {

    private val usd = CurrencyCode("USD")
    private val p1 = Participant(ParticipantId("p1"), "Alice")
    private val p2 = Participant(ParticipantId("p2"), "Bob")
    private val p3 = Participant(ParticipantId("p3"), "Carol")

    @Test
    fun `equal split conserves total cents including remainder`() {
        val total = Money(Amount(100), usd)
        val shares = SplitStrategy.resolve(SplitStrategy.EqualSplit, listOf(p1, p2, p3), total)

        val sum = shares.values.fold(Amount.ZERO) { acc, money -> acc + money.amount }
        assertEquals(total.amount, sum)
        assertEquals(Amount(34), shares.getValue(p1.id).amount)
        assertEquals(Amount(33), shares.getValue(p2.id).amount)
        assertEquals(Amount(33), shares.getValue(p3.id).amount)
    }

    @Test
    fun `custom split returns shares matching participant keys`() {
        val total = Money(Amount(100), usd)
        val strategy = SplitStrategy.CustomSplit(
            mapOf(p1.id to Money(Amount(60), usd), p2.id to Money(Amount(40), usd)),
        )

        val shares = SplitStrategy.resolve(strategy, listOf(p1, p2), total)
        assertEquals(strategy.shares, shares)
    }

    @Test
    fun `custom split rejects mismatched participant keys`() {
        val total = Money(Amount(100), usd)
        val strategy = SplitStrategy.CustomSplit(
            mapOf(p1.id to Money(Amount(60), usd), p2.id to Money(Amount(40), usd)),
        )

        assertFailsWith<IllegalArgumentException> {
            SplitStrategy.resolve(strategy, listOf(p1, p3), total)
        }
    }

    @Test
    fun `custom split allows shares that do not sum to total`() {
        val total = Money(Amount(100), usd)
        val strategy = SplitStrategy.CustomSplit(
            mapOf(p1.id to Money(Amount(60), usd), p2.id to Money(Amount(30), usd)),
        )

        val shares = SplitStrategy.resolve(strategy, listOf(p1, p2), total)

        assertEquals(strategy.shares, shares)
    }

    @Test
    fun `custom split rejects shares in a different currency`() {
        val total = Money(Amount(100), usd)
        val strategy = SplitStrategy.CustomSplit(
            mapOf(p1.id to Money(Amount(100), CurrencyCode("EUR"))),
        )

        assertFailsWith<IllegalArgumentException> {
            SplitStrategy.resolve(strategy, listOf(p1), total)
        }
    }
}
