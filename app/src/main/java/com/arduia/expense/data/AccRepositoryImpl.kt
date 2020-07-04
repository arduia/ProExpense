package com.arduia.expense.data

import com.arduia.expense.data.local.*
import kotlinx.coroutines.flow.Flow

class AccRepositoryImpl(
    private val transDao: ExpenseDao
    ) : AccRepository{

    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        transDao.insertExpense(expenseEnt)
    }

    override suspend fun getExpense(id: Int): Flow<ExpenseEnt> {
       return transDao.getItemExpense(id)
    }

    override suspend fun getAllExpense() = transDao.getAllExpense()

    override suspend fun getRecentExpense(): Flow<List<ExpenseEnt>> {
        return transDao.getRecentExpense()
    }

    override suspend fun updateExpense(expenseEnt: ExpenseEnt) {
        transDao.updateExpense(expenseEnt)
    }

    override suspend fun deleteExpense(expenseEnt: ExpenseEnt) {
        transDao.deleteExpense(expenseEnt)
    }

    override suspend fun deleteAllExpense(list: List<Int>) {
        transDao.deleteExpenseByIDs(list)
    }
}
