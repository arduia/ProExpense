package com.arduia.myacc.data.local

import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    @Query ( "SELECT * FROM `expense` ORDER BY created_date DESC" )
    fun getAllExpense(): DataSource.Factory<Int, ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE expense_id =:id")
    fun getItemExpense(id: Int): Flow<ExpenseEnt>

    @Query( "SELECT * FROM `expense` ORDER BY created_date DESC LIMIT 10")
    fun getRecentExpense(): Flow<List<ExpenseEnt>>

    @Update
    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    @Delete
    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    @Query( "DELETE FROM `expense` WHERE  expense_id in (:idLists)")
    suspend fun deleteExpenseByIDs(idLists: List<Int>)

}
