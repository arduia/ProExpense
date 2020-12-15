package com.arduia.expense.domain

data class ExpenseLogItemEnt(
    val expenseId: Int = 0,

    val name: String?,

    val amount: Amount,

    val category: Int,

    val note: String?,

    val createdDate: Long,

    val modifiedDate: Long
)
