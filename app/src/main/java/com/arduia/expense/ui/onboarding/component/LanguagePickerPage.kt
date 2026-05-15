package com.arduia.expense.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.language.LanguageRow
import com.arduia.expense.ui.component.language.LanguageUiModel

@Composable
fun LanguagePickerPage(
    languages: List<LanguageUiModel>,
    selectedCode: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
            text = "Choose Your Language",
            style = MaterialTheme.typography.headlineMedium,
        )
        LazyColumn {
            items(languages, key = { it.code }) { language ->
                LanguageRow(
                    language = language,
                    selected = language.code == selectedCode,
                    onSelect = { onSelect(it.code) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLanguagePickerPage() {
    ProExpenseTheme {
        LanguagePickerPage(
            languages = listOf(
                LanguageUiModel(code = "en", nativeName = "English", englishName = "English"),
                LanguageUiModel(code = "my", nativeName = "မြန်မာ", englishName = "Burmese"),
            ),
            selectedCode = "en",
            onSelect = {},
        )
    }
}
