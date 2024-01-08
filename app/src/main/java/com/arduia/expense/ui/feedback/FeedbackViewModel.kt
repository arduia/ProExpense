package com.arduia.expense.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.work.*
import com.arduia.expense.data.FeedbackWorker
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.EventUnit
import com.arduia.mvvm.post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val workManager: WorkManager)
    : ViewModel(){

    private val _feedbackSubmittedEvent = EventLiveData<Unit>()
    val feedbackSubmittedEvent = _feedbackSubmittedEvent.asLiveData()

    fun sendFeedback(name: String, email: String, comment: String){
        val data = createInputDataForWorker(name, email, comment)
        startFeedbackWork(data)
    }

    private fun startFeedbackWork(data: Data){
        val constraints = getWorkConstraint()
        val request = createFeedbackWorkRequest(data, constraints)
        submitWork(request)
        feedbackHasBeenSubmitted()
    }

    private fun submitWork(request: WorkRequest){
        workManager.enqueue(request)
    }

    private fun createInputDataForWorker(name: String, email: String, comment: String) =
        Data.Builder()
            .putString(FeedbackWorker.PARAM_NAME, name)
            .putString(FeedbackWorker.PARAM_EMAIL, email)
            .putString(FeedbackWorker.PARAM_COMMENT, comment)
            .build()

    private fun feedbackHasBeenSubmitted(){
        _feedbackSubmittedEvent post EventUnit
    }

    private fun createFeedbackWorkRequest(data: Data, constraints: Constraints) =
        OneTimeWorkRequestBuilder<FeedbackWorker>()
        .setConstraints(constraints)
        .setInputData(data)
        .build()

    private fun getWorkConstraint() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
