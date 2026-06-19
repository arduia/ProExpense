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
    fun calculateBudgetProgress_onBudget_usesOnBudgetTier() {
        val progress = calculateBudgetProgress(spentCents = 5000, budgetCents = 10000, periodDays = 10)
        assertEquals(50f, progress.progressPercent)
        assertEquals(BudgetColorTier.ON_BUDGET, progress.colorTier)
    }

    @Test
    fun calculateBudgetProgress_significantlyOver_usesHighestTier() {
        val progress = calculateBudgetProgress(spentCents = 12000, budgetCents = 10000, periodDays = 10)
        assertEquals(120f, progress.progressPercent)
        assertEquals(BudgetColorTier.SIGNIFICANTLY_OVER, progress.colorTier)
    }
}
