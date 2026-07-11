package com.arduia.expense.domain

enum class DebtDirection {
    OWED_TO_ME,
    I_OWE,
}

data class Debt(
    val id: DebtId,
    val personName: String,
    val money: Money,
    val direction: DebtDirection,
    val dueEpochMillis: Long? = null,
    val isSettled: Boolean = false,
    val note: String? = null,
    val recordedAtEpochMillis: Long = 0L,
    /**
     * When true, this debt also has a linked [FinanceRecord] (via [RecordLink.ToDebt]) counting
     * toward spend/income totals — mirrors how Shared Costs always does. Off by default: a debt is
     * a lending record, not necessarily real spending.
     */
    val recordAsTransaction: Boolean = false,
) {
    init {
        require(personName.isNotBlank()) { "Debt personName must not be blank" }
    }
}
