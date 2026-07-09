package com.arduia.expense.ui.design

import kotlin.test.Test
import kotlin.test.assertEquals

class EventBudgetToneTest {
    @Test
    fun eventBudgetTone_atOrUnderBudget_isOnTrack() {
        assertEquals(EventBudgetTone.OnTrack, eventBudgetTone(0f))
        assertEquals(EventBudgetTone.OnTrack, eventBudgetTone(1f))
    }

    @Test
    fun eventBudgetTone_upToTenPercentOver_isOverBudget() {
        assertEquals(EventBudgetTone.OverBudget, eventBudgetTone(1.05f))
        assertEquals(EventBudgetTone.OverBudget, eventBudgetTone(1.1f))
    }

    @Test
    fun eventBudgetTone_pastTenPercentOver_isSignificantlyOver() {
        assertEquals(EventBudgetTone.SignificantlyOver, eventBudgetTone(1.11f))
        assertEquals(EventBudgetTone.SignificantlyOver, eventBudgetTone(2f))
    }
}
