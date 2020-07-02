package com.arduia.myacc.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.arduia.myacc.data.local.*
import kotlinx.coroutines.flow.Flow

class AccRepositoryImpl(
    private val transDao: TransactionDao,
    private val peopleDao: OwePeopleDao,
    private val oweLogDao: OweLogDao
    ) : AccRepository{

    override suspend fun insertTransaction(transaction: Transaction) {
        transDao.insertTransaction(transaction)
    }

    override suspend fun getTransaction(id: Int): Flow<Transaction> {
       return transDao.getItemTransaction(id)
    }

    override suspend fun getAllTransaction() =  Pager(
        config = PagingConfig(pageSize = 30),
        pagingSourceFactory = { transDao.getAllTransaction() }
    ).flow

    override suspend fun getRecentTransaction(): Flow<List<Transaction>> {
        return transDao.getRecentTransaction()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transDao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transDao.deleteTransaction(transaction)
    }

    override suspend fun deleteAllTransaction(list: List<Int>) {
        transDao.deleteTransactionByIDs(list)
    }

    override suspend fun insertOwePeople(people: OwePeople) {
        peopleDao.insertOwePeople(people)
    }

    override suspend fun getAllOwePeople() =
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = { peopleDao.getAllOwePeople() }
        ).flow

    override suspend fun updateOwePeople(people: OwePeople) {
        peopleDao.updateOwePeople(people)
    }

    override suspend fun deleteOwePeople(people: OwePeople) {
        peopleDao.deleteOwePeople(people)
    }

    override suspend fun insertOweLog(log: OweLog) {
        oweLogDao.insertOweLog(log)
    }

    override suspend fun getAllOweLog() =
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = { oweLogDao.getAllOweLog() }
        ).flow

    override suspend fun updateOweLog(log: OweLog) {
        oweLogDao.updateOweLog(log)
    }

    override suspend fun deleteOweLog(log: OweLog) {
        oweLogDao.deleteOweLog(log)
    }

}
