package com.arduia.expense.ui.expenselogs

sealed class ExpenseLogUiModel {

    class Header(val date: String) : ExpenseLogUiModel()

    class Log(
        val expenseLog: ExpenseUiModel,
        val headerPosition: Int
    ) : ExpenseLogUiModel()

}