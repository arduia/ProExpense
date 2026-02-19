package com.arduia.expense.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun LanguageDialogContent(
    languages: List<String> = emptyList(),
    selectedLanguage: String = "",
    onLanguageSelected: (String) -> Unit = {},
    onRestartClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    // Virtually identical to ChooseLanguageDialog, but mapping the specific legacy fragment
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.choose_language),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drop_down),
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // XML says 300dp
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(languages) { language ->
                    LanguageItem(
                        name = language,
                        isSelected = language == selectedLanguage,
                        onClick = { onLanguageSelected(language) }
                    )
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProExpenseButton(
                text = stringResource(id = R.string.restart),
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageDialogContentPreview() {
    ProExpenseTheme {
        LanguageDialogContent(
            languages = listOf("English", "Myanmar (Burmese)"),
            selectedLanguage = "English"
        )
    }
}
