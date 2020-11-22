package com.arduia.expense.data.local

import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    @Insert
    suspend fun insertExpenseAll(expenses: List<ExpenseEnt>)

    @Query ( "SELECT * FROM `expense` ORDER BY created_date DESC" )
    fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    @Query ( "SELECT * FROM `expense` ORDER BY created_date DESC" )
    fun getExpenseAll(): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM `expense` WHERE expense_id =:id")
    fun getItemExpense(id: Int): Flow<ExpenseEnt>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC LIMIT 4")
    fun getRecentExpense(): Flow<List<ExpenseEnt>>

    @Query("SELECT COUNT(*) FROM expense")
    fun getExpenseTotalCount(): Flow<Int>

    @Query("SELECT * FROM 'expense' ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRange(limit: Int, offset: Int): Flow<List<ExpenseEnt>>

    @Update
    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    @Delete
    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    @Query("DELETE FROM `expense` WHERE expense_id =:id" )
    suspend fun deleteExpenseRowById(id:Int)

    @Query( "DELETE FROM `expense` WHERE  expense_id in (:idLists)")
    suspend fun deleteExpenseByIDs(idLists: List<Int>)

    @Query("SELECT * FROM 'expense' WHERE created_date > :startTime ORDER BY modified_date DESC")
    fun getWeekExpense(startTime: Long): Flow<List<ExpenseEnt>>

}
