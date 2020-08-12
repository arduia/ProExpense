package com.arduia.expense.data


import com.arduia.expense.data.local.*
import com.arduia.expense.data.network.ExpenseNetworkDao
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import java.util.*

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val networkDao: ExpenseNetworkDao
    ) : ExpenseRepository{

    private val feedbackBroadCast = BroadcastChannel<FeedbackDto.Response>(10)


    private val versionBroadCast = BroadcastChannel<ExpenseVersionDto>(10)

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

    override suspend fun deleteExpenseById(id: Int) {
        expenseDao.deleteExpenseRowById(id)
    }

    override suspend fun deleteAllExpense(list: List<Int>) {
        expenseDao.deleteExpenseByIDs(list)
    }

    override suspend fun getWeekExpenses(): Flow<List<ExpenseEnt>> {
        return expenseDao.getWeekExpense(getWeekStartTime())
    }

    override suspend fun postFeedback(comment: FeedbackDto.Request): Flow<FeedbackDto.Response> {
         postComment(comment)
        return feedbackBroadCast.asFlow()
    }

    override suspend fun getVersionStatus(): Flow<ExpenseVersionDto> {

         checkVersion()
        return versionBroadCast.asFlow()
    }

    private suspend fun checkVersion(){
        val version = networkDao.getVersionStatus().execute()
        version.body()?.let {
            Timber.d("version -> $it")
            versionBroadCast.send(it)
        }
        Timber.d("checkVersion -> $version")
    }

    private suspend fun postComment(comment: FeedbackDto.Request){
        val comment = networkDao.postFeedback(comment).execute()

        comment.body()?.let {
            feedbackBroadCast.send(it)
        }
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
