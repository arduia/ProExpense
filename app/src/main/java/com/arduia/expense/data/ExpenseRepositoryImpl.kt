package com.arduia.expense.data


import androidx.paging.DataSource
import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.ext.getResultSuccessOrError
import com.arduia.expense.data.local.*
import com.arduia.expense.data.network.ExpenseNetworkDao
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.exp

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val networkDao: ExpenseNetworkDao
) : ExpenseRepository {


    override suspend fun insertExpense(expenseEnt: ExpenseEnt) {
        expenseDao.insertExpense(expenseEnt)
    }

    override suspend fun insertExpenseAll(expenses: List<ExpenseEnt>) {
        expenseDao.insertExpenseAll(expenses)
    }

    override fun getExpenseAll(): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getExpenseAll()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override fun getExpenseRangeAsc(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ) = expenseDao.getExpenseRangeAsc(startTime, endTime, offset, limit)
        .map { SuccessResult(it) }
        .catch { ErrorResult(RepositoryException(it)) }

    override fun getExpenseRangeDesc(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ) = expenseDao.getExpenseRangeDesc(startTime, endTime, offset, limit)
        .map { SuccessResult(it) }
        .catch { ErrorResult(RepositoryException(it)) }

    override fun getExpenseRangeAscSource(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): DataSource.Factory<Int, ExpenseEnt> {
        return expenseDao.getExpenseRangeAscSource(startTime, endTime, offset, limit)
    }

    override fun getExpenseRangeDescSource(
        startTime: Long,
        endTime: Long,
        offset: Int,
        limit: Int
    ): DataSource.Factory<Int, ExpenseEnt> {
        return expenseDao.getExpenseRangeDescSource(startTime, endTime, offset, limit)
    }

    override fun getExpense(id: Int): FlowResult<ExpenseEnt> {
        return expenseDao.getItemExpense(id)
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt> {
        return expenseDao.getExpenseSourceAll()
    }

    override suspend fun getExpenseAllSync(): Result<List<ExpenseEnt>> {
        return getResultSuccessOrError { expenseDao.getExpenseAllSync() }
    }

    override fun getRecentExpense(): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getRecentExpense()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override fun getExpenseTotalCount(): FlowResult<Int> {
        return expenseDao.getExpenseTotalCount()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override suspend fun getMostRecentDateSync(): Result<Long> {
        return try {
            SuccessResult(expenseDao.getMostRecentDateSync())
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }

    override suspend fun getMostLatestDateSync(): Result<Long> {
        return try {
            SuccessResult(expenseDao.getMostLatestDateSync())
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }

    override fun getExpenseRange(limit: Int, offset: Int): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getExpenseRange(limit, offset)
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
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

    override fun getWeekExpenses(): FlowResult<List<ExpenseEnt>> {
        return expenseDao.getWeekExpense(getWeekStartTime())
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override fun postFeedback(comment: FeedbackDto.Request): FlowResult<FeedbackDto.Response> =
        flow {
            val comment = networkDao.postFeedback(comment).execute()
            comment.body()?.let {
                emit(it)
            } ?: throw Exception("Network Response Error")
        }.map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }

    override fun getVersionStatus(): FlowResult<ExpenseVersionDto> = flow {
        val version = networkDao.getVersionStatus().execute()
        version.body()?.let {
            emit(it)
        } ?: throw Exception("Network Response Error")
    }.map { SuccessResult(it) }
        .catch { e -> ErrorResult(RepositoryException(e)) }


    private fun getWeekStartTime(): Long {

        val calendar = Calendar.getInstance()

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val startSunDay = (dayOfYear - dayOfWeek) + 1

        calendar.set(Calendar.DAY_OF_YEAR, startSunDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }


}
