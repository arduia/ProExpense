package com.arduia.expense.ui.feedback.molecule

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.expense.R
import com.arduia.expense.data.FeedbackWorker
import com.arduia.expense.ui.common.molecule.Presenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

sealed interface FeedbackEvent {
    data class UpdateName(val name: String) : FeedbackEvent
    data class UpdateEmail(val email: String) : FeedbackEvent
    data class UpdateComment(val comment: String) : FeedbackEvent
    data object SubmitFeedback : FeedbackEvent
    data object DismissSuccessDialog : FeedbackEvent
}

data class FeedbackState(
    val name: String = "",
    val email: String = "",
    val comment: String = "",
    val emailErrorResId: Int? = null,
    val commentErrorResId: Int? = null,
    val isSuccessDialogVisible: Boolean = false,
    val isSubmitEnabled: Boolean = false
)

class FeedbackPresenter(
    private val workManager: WorkManager
) : Presenter<FeedbackEvent, FeedbackState> {

    @Composable
    override fun present(events: Flow<FeedbackEvent>): FeedbackState {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var comment by remember { mutableStateOf("") }
        var emailErrorResId by remember { mutableStateOf<Int?>(null) }
        var commentErrorResId by remember { mutableStateOf<Int?>(null) }
        var isSuccessDialogVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is FeedbackEvent.UpdateName -> {
                        name = event.name
                    }
                    is FeedbackEvent.UpdateEmail -> {
                        email = event.email
                        if (emailErrorResId != null) emailErrorResId = null // clear error on type
                    }
                    is FeedbackEvent.UpdateComment -> {
                        comment = event.comment
                        if (commentErrorResId != null) commentErrorResId = null // clear error on type
                    }
                    FeedbackEvent.SubmitFeedback -> {
                        emailErrorResId = null
                        commentErrorResId = null
                        var isValid = true

                        val trimmedEmail = email.trim()
                        val trimmedComment = comment.trim()
                        val trimmedName = name.trim() // Name can be empty

                        if (trimmedEmail.isNotEmpty()) {
                            val valid = Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
                            if (!valid) {
                                emailErrorResId = R.string.invalid_email
                                isValid = false
                            }
                        }
                        // If email is empty, we don't invalidate it.

                        if (trimmedComment.isEmpty()) {
                            commentErrorResId = R.string.empty_comment
                            isValid = false
                        }

                        if (isValid) {
                            val data = Data.Builder()
                                .putString(FeedbackWorker.PARAM_NAME, trimmedName)
                                .putString(FeedbackWorker.PARAM_EMAIL, trimmedEmail)
                                .putString(FeedbackWorker.PARAM_COMMENT, trimmedComment)
                                .build()

                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()

                            val request = OneTimeWorkRequestBuilder<FeedbackWorker>()
                                .setConstraints(constraints)
                                .setInputData(data)
                                .build()

                            workManager.enqueue(request)
                            
                            kotlinx.coroutines.delay(300)
                            isSuccessDialogVisible = true
                            
                            // Clear fields after successful submission
                            name = ""
                            email = ""
                            comment = ""
                            emailErrorResId = null
                            commentErrorResId = null
                        }
                    }
                    FeedbackEvent.DismissSuccessDialog -> {
                        isSuccessDialogVisible = false
                    }
                }
            }
        }

        val trimmedEmailForState = email.trim()
        val isEmailValid = trimmedEmailForState.isEmpty()
                || Patterns.EMAIL_ADDRESS.matcher(trimmedEmailForState).matches()
        val isCommentValid = comment.trim().isNotEmpty()

        return FeedbackState(
            name = name,
            email = email,
            comment = comment,
            emailErrorResId = emailErrorResId,
            commentErrorResId = commentErrorResId,
            isSuccessDialogVisible = isSuccessDialogVisible,
            isSubmitEnabled = isEmailValid && isCommentValid
        )
    }
}
