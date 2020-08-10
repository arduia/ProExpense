package com.arduia.expense.data

import androidx.paging.DataSource
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import kotlinx.coroutines.flow.Flow

interface AccRepository {

    //Expense
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    suspend fun insertExpenseAll(expenses: List<ExpenseEnt>)

    suspend fun getExpense(id: Int): Flow<ExpenseEnt>

    suspend fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    suspend fun getExpenseAll(): Flow<List<ExpenseEnt>>

    suspend fun getRecentExpense(): Flow<List<ExpenseEnt>>

    suspend fun getExpenseTotalCount(): Flow<Int>

    suspend fun getExpenseRange(limit: Int, offset: Int): Flow<List<ExpenseEnt>>

    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpenseById(id: Int)

    suspend fun deleteAllExpense(list: List<Int>)

    suspend fun getWeekExpenses(): Flow<List<ExpenseEnt>>

    suspend fun postFeedback(comment: FeedbackDto.Request): Flow<FeedbackDto.Response>

    suspend fun getVersionStatus(): Flow<ExpenseVersionDto>

}
