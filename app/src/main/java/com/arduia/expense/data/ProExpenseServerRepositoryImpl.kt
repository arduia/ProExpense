package com.arduia.expense.data

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.data.network.ExpenseNetworkDao
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onErrorResumeNext
import javax.inject.Inject

class ProExpenseServerRepositoryImpl @Inject constructor(
    private val networkDao: ExpenseNetworkDao
) : ProExpenseServerRepository {
    override fun postFeedback(comment: FeedbackDto.Request): FlowResult<FeedbackDto.Response> =
        flow{
            try {
                val comment = networkDao.postFeedback(comment)
                emit(comment)
            }catch (e: Throwable){
                e.printStackTrace()
                throw e
            }
        }
            .map{
                SuccessResult(it) as Result<FeedbackDto.Response> }
            .catch { e ->
                emit(ErrorResult(RepositoryException(e)))
            }

    override fun getVersionStatus(): FlowResult<ExpenseVersionDto> = flow {
        val version = networkDao.getVersionStatus()
        emit(version)
    }.map { SuccessResult(it) }
        .catch { e -> ErrorResult(RepositoryException(e)) }


    override suspend fun getAboutUpdateSync(deviceInfo: CheckUpdateDto.Request): Result<CheckUpdateDto.Response> {
        return try {
            val info = networkDao.getCheckUpdateInfo(deviceInfo)
            SuccessResult(info)
        } catch (e: java.lang.Exception) {
            ErrorResult(e)
        }
    }
}