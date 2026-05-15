package com.arduia.expense.ui.feedback

data class FeedbackUiState(
    val feedbackText: String = "",
    val email: String = "",
    val isSending: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val feedbackError: String? = null,
)

sealed interface FeedbackUiEvent {
    data object SendSuccess : FeedbackUiEvent
    data class ShowError(val message: String) : FeedbackUiEvent
}
