package com.arduia.expense.data

import androidx.paging.DataSource
import androidx.room.Query
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    //Expense
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    suspend fun insertExpenseAll(expenses: List<ExpenseEnt>)

    fun getExpense(id: Int): FlowResult<ExpenseEnt>

    fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    fun getExpenseAll(): FlowResult<List<ExpenseEnt>>

    suspend fun getExpenseAllSync(): Result<List<ExpenseEnt>>

    fun getRecentExpense(): FlowResult<List<ExpenseEnt>>

    fun getExpenseTotalCount(): FlowResult<Int>

    suspend fun getMostRecentDateSync(): Result<Long>

    suspend fun getMostLatestDateSync(): Result<Long>

    fun getExpenseRange(limit: Int, offset: Int): FlowResult<List<ExpenseEnt>>

    fun getExpenseRangeAsc(startTime: Long, endTime: Long, offset: Int, limit: Int): FlowResult<List<ExpenseEnt>>

    fun getExpenseRangeDesc(startTime: Long, endTime: Long, offset: Int, limit: Int): FlowResult<List<ExpenseEnt>>

    fun getExpenseRangeAscSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    fun getExpenseRangeDescSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    suspend fun deleteExpenseById(id: Int)

    suspend fun deleteAllExpense(list: List<Int>)

    fun getWeekExpenses(): FlowResult<List<ExpenseEnt>>



}
