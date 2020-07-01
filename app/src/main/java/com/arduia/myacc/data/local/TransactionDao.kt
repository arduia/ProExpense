package com.arduia.myacc.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.arduia.myacc.data.local.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertTransaction(transaction: Transaction)

    @Query ( "SELECT * FROM `transaction` ORDER BY created_date DESC" )
    fun getAllTransaction(): PagingSource<Int, Transaction>

    @Query("SELECT * FROM `transaction` WHERE transaction_id =:id")
    fun getItemTransaction(id: Int): Flow<Transaction>

    @Query( "SELECT * FROM `transaction` ORDER BY created_date DESC LIMIT 10")
    fun getRecentTransaction(): Flow<List<Transaction>>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query( "DELETE FROM `transaction` WHERE  transaction_id in (:idLists)")
    suspend fun deleteTransactionByIDs(idLists: List<Int>)

}
