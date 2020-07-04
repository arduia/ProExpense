package com.arduia.myacc.data

import androidx.lifecycle.asFlow
import androidx.paging.LivePagedListBuilder
import com.arduia.myacc.data.local.*
import kotlinx.coroutines.flow.Flow

class AccRepositoryImpl(
    private val transDao: TransactionDao
    ) : AccRepository{

    override suspend fun insertTransaction(transaction: Transaction) {
        transDao.insertTransaction(transaction)
    }

    override suspend fun getTransaction(id: Int): Flow<Transaction> {
       return transDao.getItemTransaction(id)
    }

    override suspend fun getAllTransaction() = transDao.getAllTransaction()

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
}
