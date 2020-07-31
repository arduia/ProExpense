package com.arduia.expense.data


import com.arduia.expense.data.local.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class AccRepositoryImpl(
    private val expenseDao: ExpenseDao
    ) : AccRepository{

    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        expenseDao.insertExpense(expenseEnt)
    }

    override suspend fun insertExpenseAll(expenses: List<ExpenseEnt>) {
        expenseDao.insertExpenseAll(expenses)
    }

    override suspend fun getExpenseAll(): Flow<List<ExpenseEnt>> {
        return expenseDao.getExpenseAll()
    }

    override suspend fun getExpense(id: Int): Flow<ExpenseEnt> {
       return expenseDao.getItemExpense(id)
    }

    override suspend fun getExpenseSourceAll() = expenseDao.getExpenseSourceAll()

    override suspend fun getRecentExpense(): Flow<List<ExpenseEnt>> {
        return expenseDao.getRecentExpense()
    }

    override suspend fun getExpenseTotalCount(): Flow<Int> {
        return expenseDao.getExpenseTotalCount()
    }

    override suspend fun getExpenseRange(limit: Int, offset: Int): Flow<List<ExpenseEnt>> {
       return expenseDao.getExpenseRange(limit, offset)
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

    override suspend fun getWeekExpenses(): Flow<List<ExpenseEnt>> {
        return expenseDao.getWeekExpense(getWeekStartTime())
    }

    private fun getWeekStartTime(): Long {

        val calendar = Calendar.getInstance()

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val startSunDay = (dayOfYear - dayOfWeek) + 1

        calendar.set(Calendar.DAY_OF_YEAR, startSunDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND,0)

        return calendar.timeInMillis
    }
}
