package com.arduia.expense.data

import com.arduia.expense.data.local.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class AccRepositoryImpl(
    private val expenseDao: ExpenseDao
    ) : AccRepository{

    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        expenseDao.insertExpense(expenseEnt)
    }

    override suspend fun getExpense(id: Int): Flow<ExpenseEnt> {
       return expenseDao.getItemExpense(id)
    }

    override suspend fun getAllExpense() = expenseDao.getAllExpense()

    override suspend fun getRecentExpense(): Flow<List<ExpenseEnt>> {
        return expenseDao.getRecentExpense()
    }

    override suspend fun updateExpense(expenseEnt: ExpenseEnt) {
        expenseDao.updateExpense(expenseEnt)
    }

    override suspend fun deleteExpense(expenseEnt: ExpenseEnt) {
        expenseDao.deleteExpense(expenseEnt)
    }

    override suspend fun deleteAllExpense(list: List<Int>) {
        expenseDao.deleteExpenseByIDs(list)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override suspend fun getCostPerWeek(startDate: Long): Flow<Long> {
      return expenseDao.getLastWeekCosts(startDate).map { it.sum() }
    }

}
