package com.arduia.expense.data

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.expense.data.network.FeedbackDto
import kotlinx.coroutines.flow.first
import timber.log.Timber

class FeedbackWorker @WorkerInject constructor(@Assisted context: Context,
                                               @Assisted param: WorkerParameters,
                                               private val repo: ExpenseRepository): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {
        val request = getFeedbackRequest() ?: return Result.failure()

        val response = repo.postFeedback(request).first()

        Timber.d("Response -> $response")
        return Result.success()
    }

    private fun getEmailArg() = inputData.getString(PARAM_EMAIL) ?: ""

    private fun getNameArg() = inputData.getString(PARAM_NAME)?: ""

    private fun getCommentArg() = inputData.getString(PARAM_COMMENT)?: ""

    private fun getFeedbackRequest(): FeedbackDto.Request?{
        val name = getNameArg()
        val email = getEmailArg()
        val comment = getCommentArg()

        if(comment.isEmpty()) return null

        return FeedbackDto.Request(name, email, comment, DEFAULT_KEY)
    }

    companion object{
        const val PARAM_EMAIL = "EMAIL"
        const val PARAM_NAME = "NAME"
        const val PARAM_COMMENT = "COMMENT"
        private const val DEFAULT_KEY = "TestKey"
    }
}
