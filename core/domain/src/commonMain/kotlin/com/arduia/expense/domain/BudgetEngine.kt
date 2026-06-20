package com.arduia.expense.domain

enum class BudgetColorTier {
    ON_BUDGET,
    SLIGHTLY_OVER,
    SIGNIFICANTLY_OVER,
}

data class BudgetProgress(
    val spentCents: Long,
    val budgetCents: Long?,
    val progressPercent: Float,
    val colorTier: BudgetColorTier?,
    val dailyAverageCents: Long,
)

fun calculateBudgetProgress(
    spentCents: Long,
    budgetCents: Long?,
    periodDays: Int,
): BudgetProgress {
    val safeDays = periodDays.coerceAtLeast(1)
    val dailyAverage = spentCents / safeDays
    if (budgetCents == null || budgetCents <= 0L) {
        return BudgetProgress(
            spentCents = spentCents,
            budgetCents = budgetCents,
            progressPercent = 0f,
            colorTier = null,
            dailyAverageCents = dailyAverage,
        )
    }
    val percent = spentCents.toFloat() / budgetCents.toFloat() * 100f
    val tier = when {
        percent <= 100f -> BudgetColorTier.ON_BUDGET
        percent <= 110f -> BudgetColorTier.SLIGHTLY_OVER
        else -> BudgetColorTier.SIGNIFICANTLY_OVER
    }
    return BudgetProgress(
        spentCents = spentCents,
        budgetCents = budgetCents,
        progressPercent = percent,
        colorTier = tier,
        dailyAverageCents = dailyAverage,
    )
}
