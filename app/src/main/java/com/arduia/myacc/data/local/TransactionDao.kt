package com.arduia.myacc.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.arduia.myacc.data.local.Transaction

@Dao
interface TransactionDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertTransaction(transaction: Transaction)

    @Query ( "SELECT * FROM `transaction`" )
    fun getAllTransaction(): PagingSource<Int, Transaction>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

}
