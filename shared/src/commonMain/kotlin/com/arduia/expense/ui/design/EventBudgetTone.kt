package com.arduia.expense.ui.design

/** Budget progress system (tokens.md): 0–100% on track, 101–110% over, 110%+ significantly over. */
enum class EventBudgetTone {
    OnTrack,
    OverBudget,
    SignificantlyOver,
}

fun eventBudgetTone(spentRatio: Float): EventBudgetTone =
    when {
        spentRatio <= 1f -> EventBudgetTone.OnTrack
        spentRatio <= 1.1f -> EventBudgetTone.OverBudget
        else -> EventBudgetTone.SignificantlyOver
    }
