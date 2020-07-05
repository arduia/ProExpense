package com.arduia.expense.data

import com.arduia.expense.data.local.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.*

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
    override suspend fun getWeeklyCostTotal(): Flow<Long> {


      return expenseDao.getLastWeekCosts(getStartWeek()).map { it.sum() }
    }

    override suspend fun getWeeklyCostRates(): Flow<Map<Int, Int>> {
        return expenseDao.getLastWeekCosts(getStartWeek()).map {
            val data  = mutableMapOf<Int,Int>()
            data[1] = 100
            data[2] = 29
            data[3] = 50
            data[3] = 5
            data[5] = 60
            data[6] = 50
            data[7] = 100
            data
        }
    }



    private fun getStartWeek(): Long{

        return 0
    }
}
