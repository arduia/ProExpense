package com.arduia.expense.domain

enum class DebtDirection {
    OWED_TO_ME,
    I_OWE,
}

data class Debt(
    val id: String,
    val personName: String,
    val amount: Amount,
    val direction: DebtDirection,
    val dueEpochMillis: Long? = null,
    val isSettled: Boolean = false,
)
