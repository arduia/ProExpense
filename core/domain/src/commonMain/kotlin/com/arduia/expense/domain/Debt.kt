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
) {
    init {
        require(personName.isNotBlank()) { "Debt personName must not be blank" }
    }
}
