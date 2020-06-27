package com.arduia.myacc.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertTransaction(transaction: Transaction)

    @Query ( "SELECT * FROM `transaction`" )
    suspend fun getAllTransaction(): List<Transaction>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

}
