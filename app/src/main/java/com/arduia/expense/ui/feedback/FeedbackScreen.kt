package com.arduia.expense.ui.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.form.LabeledTextField
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.dialog.FeedbackStatusDialog

@Composable
fun FeedbackScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeedbackContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onFeedbackChange = viewModel::onFeedbackChange,
        onEmailChange = viewModel::onEmailChange,
        onSend = viewModel::onSend,
        onSuccessDismiss = viewModel::onSuccessDismiss,
        modifier = modifier,
    )
}

@Composable
private fun FeedbackContent(
    uiState: FeedbackUiState,
    onNavigateBack: () -> Unit,
    onFeedbackChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onSuccessDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = "Feedback",
                navIcon = TopBarNavIcon.Back,
                onNavIconClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(ProExpenseTheme.spacing.grid3)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        ) {
            LabeledTextField(
                label = "Email (optional)",
                value = uiState.email,
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            LabeledTextField(
                label = "Feedback",
                value = uiState.feedbackText,
                onValueChange = onFeedbackChange,
                isError = uiState.feedbackError != null,
                errorMessage = uiState.feedbackError,
                singleLine = false,
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSend,
                enabled = !uiState.isSending,
            ) {
                Text(text = "Send")
            }
        }
        if (uiState.showSuccessDialog) {
            FeedbackStatusDialog(
                title = "Feedback Sent",
                message = "Thank you! We appreciate your feedback.",
                isSuccess = true,
                onDismiss = onSuccessDismiss,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFeedbackContent() {
    ProExpenseTheme {
        FeedbackContent(
            uiState = FeedbackUiState(),
            onNavigateBack = {},
            onFeedbackChange = {},
            onEmailChange = {},
            onSend = {},
            onSuccessDismiss = {},
        )
    }
}
