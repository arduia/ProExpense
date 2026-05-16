package com.arduia.expense.ui.feedback

import androidx.work.*
import com.arduia.expense.data.FeedbackWorker
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class FeedbackUiState(
    val isSending: Boolean = false
)

sealed class FeedbackUiEffect {
    object FeedbackSubmitted : FeedbackUiEffect()
}

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val workManager: WorkManager)
    : BaseViewModel<FeedbackUiState, FeedbackUiEffect>(FeedbackUiState()) {

    fun sendFeedback(name: String, email: String, comment: String) {
        val data = createInputDataForWorker(name, email, comment)
        startFeedbackWork(data)
    }

    private fun startFeedbackWork(data: Data) {
        val constraints = getWorkConstraint()
        val request = createFeedbackWorkRequest(data, constraints)
        submitWork(request)
        sendEffect(FeedbackUiEffect.FeedbackSubmitted)
    }

    private fun submitWork(request: WorkRequest) {
        workManager.enqueue(request)
    }

    private fun createInputDataForWorker(name: String, email: String, comment: String) =
        Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, name)
            .putString(FeedbackWorker.PARAM_EMAIL, email)
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()

    private fun createFeedbackWorkRequest(data: Data, constraints: Constraints) =
        OneTimeWorkRequestBuilder<FeedbackWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

    private fun getWorkConstraint() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun onFeedbackChange(text: String) {}
    fun onEmailChange(email: String) {}
    fun onSend() {}
    fun onSuccessDismiss() {}
}
