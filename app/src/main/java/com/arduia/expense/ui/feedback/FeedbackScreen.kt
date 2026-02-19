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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onNavigationIconClick: () -> Unit = {},
    name: String = "",
    onNameChange: (String) -> Unit = {},
    email: String = "",
    onEmailChange: (String) -> Unit = {},
    comment: String = "",
    onCommentChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {}
) {
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
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // @dimen/grid_3 horizontal, grid_3 top/bottom roughly
                    label = stringResource(id = R.string.name),
                    singleLine = true
                )

                // Email Input
                ProExpenseTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = stringResource(id = R.string.email),
                    singleLine = true
                )

                // Comment Input
                ProExpenseTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = stringResource(id = R.string.your_comments), // hint in XML
                    placeholder = stringResource(id = R.string.your_comments),
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Send Button
                ProExpenseButton(
                    onClick = onSendClick,
                    text = stringResource(id = R.string.send_feedback),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackScreenPreview() {
    ProExpenseTheme {
        FeedbackScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun FeedbackScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        FeedbackScreen()
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun FeedbackScreenBurmesePreview() {
    ProExpenseTheme {
        FeedbackScreen()
    }
}
