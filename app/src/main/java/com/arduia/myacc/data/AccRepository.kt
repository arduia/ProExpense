package com.arduia.myacc.data

import androidx.paging.PagingData
import com.arduia.myacc.data.local.OweLog
import com.arduia.myacc.data.local.OwePeople
import com.arduia.myacc.data.local.Transaction
import kotlinx.coroutines.flow.Flow

interface AccRepository {

    //Transaction
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getAllTransaction(): Flow<PagingData<Transaction>>

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    //OwePeople
    suspend fun insertOwePeople(people: OwePeople)

    suspend fun getAllOwePeople(): Flow<PagingData<OwePeople>>

    suspend fun updateOwePeople(people: OwePeople)

    suspend fun deleteOwePeople(people: OwePeople)

    //Owe Log
    suspend fun insertOweLog(log: OweLog)

    suspend fun getAllOweLog(): Flow<PagingData<OweLog>>

    suspend fun updateOweLog(log: OweLog)

    suspend fun deleteOweLog(log: OweLog)
}
