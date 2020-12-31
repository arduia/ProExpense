package com.arduia.expense.ui.expenselogs

sealed class ExpenseLogVo {

    class Header(val date: String) : ExpenseLogVo()

    class Log(
        val expenseLog: ExpenseVto,
        val headerPosition: Int
    ) : ExpenseLogVo()

}