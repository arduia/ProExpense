/**
 * Domain rules (design plan D10 / Reports):
 * - Daily average = spentCents ÷ periodDays (minimum 1 day).
 * - Color tiers: ≤100% ON_BUDGET, 101–110% SLIGHTLY_OVER, >110% SIGNIFICANTLY_OVER.
 * - Null or zero budget → no color tier (progress 0%).
 */
package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BudgetEngineTest {
    @Test
    fun calculateBudgetProgress_withoutBudget_returnsZeroPercent() {
        val progress = calculateBudgetProgress(spentCents = 5000, budgetCents = null, periodDays = 30)
        assertEquals(0f, progress.progressPercent)
        assertNull(progress.colorTier)
        assertEquals(166L, progress.dailyAverageCents)
    }

    @Test
    fun calculateBudgetProgress_zeroBudget_returnsNullTier() {
        val progress = calculateBudgetProgress(spentCents = 5000, budgetCents = 0L, periodDays = 30)
        assertEquals(0f, progress.progressPercent)
        assertNull(progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_onBudget_usesOnBudgetTier() {
        val progress = calculateBudgetProgress(spentCents = 5000, budgetCents = 10000, periodDays = 10)
        assertEquals(50f, progress.progressPercent)
        assertEquals(BudgetColorTier.ON_BUDGET, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_exactly100Percent_staysOnBudget() {
        val progress = calculateBudgetProgress(spentCents = 10000, budgetCents = 10000, periodDays = 10)
        assertEquals(100f, progress.progressPercent)
        assertEquals(BudgetColorTier.ON_BUDGET, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_slightlyOver_usesYellowTier() {
        val progress = calculateBudgetProgress(spentCents = 10500, budgetCents = 10000, periodDays = 10)
        assertEquals(105f, progress.progressPercent, 0.01f)
        assertEquals(BudgetColorTier.SLIGHTLY_OVER, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_exactly110Percent_staysSlightlyOver() {
        val progress = calculateBudgetProgress(spentCents = 11000, budgetCents = 10000, periodDays = 10)
        assertEquals(110f, progress.progressPercent, 0.01f)
        assertEquals(BudgetColorTier.SLIGHTLY_OVER, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_significantlyOver_usesHighestTier() {
        val progress = calculateBudgetProgress(spentCents = 12000, budgetCents = 10000, periodDays = 10)
        assertEquals(120f, progress.progressPercent, 0.01f)
        assertEquals(BudgetColorTier.SIGNIFICANTLY_OVER, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_zeroPeriodDays_coercesToOneDayAverage() {
        val progress = calculateBudgetProgress(spentCents = 100, budgetCents = 1000, periodDays = 0)
        assertEquals(100L, progress.dailyAverageCents)
    }
}
