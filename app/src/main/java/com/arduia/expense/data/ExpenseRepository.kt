package com.arduia.expense.data

import androidx.paging.DataSource
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.FlowResult
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    //Expense
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    suspend fun insertExpenseAll(expenses: List<ExpenseEnt>)

    fun getExpense(id: Int): FlowResult<ExpenseEnt>

    fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    fun getExpenseAll(): FlowResult<List<ExpenseEnt>>

    fun getRecentExpense(): FlowResult<List<ExpenseEnt>>

    fun getExpenseTotalCount(): FlowResult<Int>

    fun getExpenseRange(limit: Int, offset: Int): FlowResult<List<ExpenseEnt>>

    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpenseById(id: Int)

    suspend fun deleteAllExpense(list: List<Int>)

    fun getWeekExpenses(): FlowResult<List<ExpenseEnt>>

    fun postFeedback(comment: FeedbackDto.Request): FlowResult<FeedbackDto.Response>

    fun getVersionStatus(): FlowResult<ExpenseVersionDto>

}
