package com.arduia.myacc.data

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.arduia.myacc.data.local.OweLog
import com.arduia.myacc.data.local.OwePeople
import com.arduia.myacc.data.local.Transaction
import kotlinx.coroutines.flow.Flow

interface AccRepository {

    //Transaction
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getTransaction(id: Int): Flow<Transaction>

    suspend fun getAllTransaction(): DataSource.Factory<Int, Transaction>

    suspend fun getRecentTransaction(): Flow<List<Transaction>>

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteAllTransaction(list: List<Int>)

    //OwePeople
    suspend fun insertOwePeople(people: OwePeople)

    suspend fun getAllOwePeople(): DataSource.Factory<Int, OwePeople>

    suspend fun updateOwePeople(people: OwePeople)

    suspend fun deleteOwePeople(people: OwePeople)

    //Owe Log
    suspend fun insertOweLog(log: OweLog)

    suspend fun getAllOweLog(): DataSource.Factory<Int, OweLog>

    suspend fun updateOweLog(log: OweLog)

    suspend fun deleteOweLog(log: OweLog)

}
