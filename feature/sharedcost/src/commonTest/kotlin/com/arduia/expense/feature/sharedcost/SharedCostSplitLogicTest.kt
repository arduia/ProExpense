package com.arduia.expense.feature.sharedcost

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedCostSplitLogicTest {
    // Rule: equal mode divides the total in integer cents across participants.
    @Test
    fun equalShareCents_dividesTotal() {
        assertEquals(3000L, SharedCostSplitLogic.equalShareCents("90", 3))
        assertEquals(2500L, SharedCostSplitLogic.equalShareCents("100.02", 4))
    }

    // Failure mode: zero or negative people count must not divide by zero.
    @Test
    fun equalShareCents_zeroPeople_returnsZero() {
        assertEquals(0L, SharedCostSplitLogic.equalShareCents("90", 0))
    }

    // Rule: cents render as symbol + whole + 2-digit-padded fraction.
    @Test
    fun formatCents_padsFraction() {
        assertEquals("$30.00", SharedCostSplitLogic.formatCents(3000))
        assertEquals("€7.05", SharedCostSplitLogic.formatCents(705, "€"))
    }

    // Rule: save is gated on a positive total.
    @Test
    fun canSave_requiresPositiveTotal() {
        assertTrue(SharedCostSplitLogic.canSave("12.50"))
        assertFalse(SharedCostSplitLogic.canSave("0"))
        assertFalse(SharedCostSplitLogic.canSave(""))
    }

    // Rule: the %1$d template substitutes the 1-based participant index (KMP-safe, no String.format).
    @Test
    fun defaultParticipantName_substitutesTemplateIndex() {
        assertEquals("Person 3", SharedCostSplitLogic.defaultParticipantName(3))
        assertEquals("Personne 2", SharedCostSplitLogic.defaultParticipantName(2, "Personne %1\$d"))
    }

    // Rule: syncNames keeps existing entries and fills the tail with defaults on resize.
    @Test
    fun syncNames_preservesExistingAndFillsDefaults() {
        val synced = SharedCostSplitLogic.syncNames(listOf("Aiko", "Ben"), 4)
        assertEquals(listOf("Aiko", "Ben", "Person 3", "Person 4"), synced)
    }

    // Rule (US-SHC-1): blank names fall back to their "Person N" default at save time.
    @Test
    fun resolveNames_blankFallsBackToDefault() {
        val resolved = SharedCostSplitLogic.resolveNames(listOf("Aiko", "  ", "Cara"), 3)
        assertEquals(listOf("Aiko", "Person 2", "Cara"), resolved)
    }

    // Rule: custom-share sync seeds missing entries with the equal share.
    @Test
    fun syncCustomShares_seedsWithEqualShare() {
        val shares = SharedCostSplitLogic.syncCustomShares(emptyList(), 2, "90")
        assertEquals(listOf("45", "45"), shares)
    }

    // Success path: equal mode labels every participant with the same formatted share.
    @Test
    fun buildParticipants_equalMode() {
        val participants =
            SharedCostSplitLogic.buildParticipants(
                rawTotal = "90",
                peopleCount = 3,
                mode = SharedSplitMode.Equal,
                names = listOf("Aiko", "Ben", "Cara"),
                customShareRaws = emptyList(),
            )
        assertEquals(
            listOf("Aiko" to "$30.00", "Ben" to "$30.00", "Cara" to "$30.00"),
            participants,
        )
    }

    // Rule: custom-share sum reports whether the split matches the total (never rebalanced).
    @Test
    fun customShareSumCents_sumsRawShares() {
        assertEquals(9000L, SharedCostSplitLogic.customShareSumCents(listOf("60", "30")))
    }

    // Rule: sums can legitimately diverge from the total since custom shares are never rebalanced.
    @Test
    fun customShareSumCents_reflectsDivergenceFromTotal() {
        assertEquals(8000L, SharedCostSplitLogic.customShareSumCents(listOf("50", "30")))
    }

    // Rule: custom mode formats each raw share as entered.
    @Test
    fun buildParticipants_customMode_usesRawShares() {
        val participants =
            SharedCostSplitLogic.buildParticipants(
                rawTotal = "90",
                peopleCount = 2,
                mode = SharedSplitMode.Custom,
                names = listOf("Aiko", "Ben"),
                customShareRaws = listOf("60", "30"),
            )
        assertEquals(listOf("Aiko" to "$60", "Ben" to "$30"), participants)
    }
}
