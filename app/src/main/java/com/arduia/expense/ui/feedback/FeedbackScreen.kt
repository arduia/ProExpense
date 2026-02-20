package com.arduia.expense.ui.feedback

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

import com.arduia.expense.ui.feedback.molecule.FeedbackEvent
import com.arduia.expense.ui.feedback.molecule.FeedbackState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    state: FeedbackState,
    onEvent: (FeedbackEvent) -> Unit,
    onNavigationIconClick: () -> Unit = {},
    onGoHomeClick: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.feedback)) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp) // @dimen/width_layout_min
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp), // @dimen/grid_2
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Description
                Text(
                    text = stringResource(id = R.string.feedback_description),
                    style = MaterialTheme.typography.bodyLarge, // Body1
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(16.dp) // @dimen/grid_3
                )

                // Name Input
                ProExpenseTextField(
                    value = state.name,
                    onValueChange = { onEvent(FeedbackEvent.UpdateName(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = stringResource(id = R.string.name),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                // Email Input
                ProExpenseTextField(
                    value = state.email,
                    onValueChange = { onEvent(FeedbackEvent.UpdateEmail(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = stringResource(id = R.string.email),
                    isError = state.emailErrorResId != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (state.emailErrorResId != null) {
                    Text(
                        text = stringResource(id = state.emailErrorResId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                }

                // Comment Input
                ProExpenseTextField(
                    value = state.comment,
                    onValueChange = { onEvent(FeedbackEvent.UpdateComment(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(120.dp),
                    label = stringResource(id = R.string.your_comments),
                    placeholder = stringResource(id = R.string.your_comments),
                    isError = state.commentErrorResId != null,
                    singleLine = false,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                if (state.commentErrorResId != null) {
                    Text(
                        text = stringResource(id = state.commentErrorResId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Send Button
                ProExpenseButton(
                    onClick = {
                        keyboardController?.hide()
                        onEvent(FeedbackEvent.SubmitFeedback)
                    },
                    text = stringResource(id = R.string.send_feedback),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = state.isSubmitEnabled
                )
            }
        }
    }

    if (state.isSuccessDialogVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onEvent(FeedbackEvent.DismissSuccessDialog) },
            sheetState = sheetState,
            dragHandle = null,
            shape = RoundedCornerShape(4.dp)
        ) {
            FeedbackStatusDialogScreen(
                onGoHomeClick = onGoHomeClick,
                onCloseClick = {
                    onEvent(FeedbackEvent.DismissSuccessDialog)
                    onGoHomeClick()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackScreenPreview() {
    ProExpenseTheme {
        FeedbackScreen(state = FeedbackState(), onEvent = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun FeedbackScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        FeedbackScreen(state = FeedbackState(), onEvent = {})
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun FeedbackScreenBurmesePreview() {
    ProExpenseTheme {
        FeedbackScreen(state = FeedbackState(), onEvent = {})
    }
}
