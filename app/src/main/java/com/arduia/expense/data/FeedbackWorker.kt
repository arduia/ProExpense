package com.arduia.expense.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.expense.data.network.FeedbackDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class FeedbackWorker @AssistedInject constructor(@Assisted context: Context,
                                                     @Assisted param: WorkerParameters,
                                                     private val serverRepo: ProExpenseServerRepository,
                                                     private val repo: ExpenseRepository): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {
        val request = getFeedbackRequest() ?: return Result.failure()
        val response = serverRepo.postFeedback(request).first()

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
