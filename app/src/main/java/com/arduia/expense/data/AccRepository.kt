package com.arduia.expense.data

import androidx.paging.DataSource
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import kotlinx.coroutines.flow.Flow

interface AccRepository {

    //Expense
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    suspend fun getExpense(id: Int): Flow<ExpenseEnt>

    suspend fun getAllExpense(): DataSource.Factory<Int, ExpenseEnt>

    suspend fun getRecentExpense(): Flow<List<ExpenseEnt>>

    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteAllExpense(list: List<Int>)

    suspend fun getWeekExpenses(): Flow<List<ExpenseEnt>>

    suspend fun postFeedback(comment: FeedbackDto.Request): Flow<FeedbackDto.Response>

    suspend fun getVersionStatus(): Flow<ExpenseVersionDto>

}
