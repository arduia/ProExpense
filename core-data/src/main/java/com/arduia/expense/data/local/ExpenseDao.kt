package com.arduia.expense.data.local

import androidx.paging.DataSource
import androidx.room.*
import com.arduia.expense.model.Result
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    fun insertExpense(expenseEnt: ExpenseEnt): Long

    @Insert
    fun insertExpenseAll(expenses: List<ExpenseEnt>): List<Long>

    @Query ( "SELECT * FROM `expense` ORDER BY modified_date DESC" )
    fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    @Query ( "SELECT * FROM `expense` ORDER BY modified_date DESC" )
    fun getExpenseAll(): Flow<List<ExpenseEnt>>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC")
    fun getExpenseAllSync(): List<ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date ASC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeAsc(startTime: Long, endTime: Long, offset: Int, limit: Int): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeDesc(startTime: Long, endTime: Long, offset: Int, limit: Int): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date ASC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeAscSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeDescSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE expense_id =:id")
    fun getItemExpense(id: Int): Flow<ExpenseEnt>

    @Query("SELECT `modified_date` FROM `expense` ORDER BY `modified_date` ASC LIMIT 1")
    fun getMostRecentDateSync(): Long?

    @Query("SELECT `modified_date` FROM `expense` ORDER BY `modified_date` DESC LIMIT 1")
    fun getMostLatestDateSync(): Long?

    @Query("SELECT `modified_date`, MIN(`modified_date`) AS `minDate`, MAX(`modified_date`) AS `maxDate` FROM `expense` LIMIT 1")
    fun getMaxAndMiniDateRange(): Flow<DateRangeDataModel>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC LIMIT 4")
    fun getRecentExpense(): Flow<List<ExpenseEnt>>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC LIMIT 4")
    fun getRecentExpenseSync(): List<ExpenseEnt>

    @Query("SELECT COUNT(*) FROM expense")
    fun getExpenseTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM expense")
    fun getExpenseTotalCountSync(): Int

    @Query("SELECT * FROM 'expense' ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRange(limit: Int, offset: Int): Flow<List<ExpenseEnt>>

    @Update
    fun updateExpense(expenseEnt: ExpenseEnt): Int

    @Delete
    fun deleteExpense(expenseEnt: ExpenseEnt): Int

    @Query("DELETE FROM `expense` WHERE expense_id =:id" )
    fun deleteExpenseRowById(id:Int): Int

    @Query( "DELETE FROM `expense` WHERE  expense_id in (:idLists)")
    fun deleteExpenseByIDs(idLists: List<Int>): Int

    @Query("SELECT * FROM 'expense' WHERE modified_date > :startTime ORDER BY modified_date DESC")
    fun getWeekExpense(startTime: Long): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM 'expense' WHERE modified_date > :startTime ORDER BY modified_date DESC")
    fun getWeekExpenseSync(startTime: Long): List<ExpenseEnt>

}
