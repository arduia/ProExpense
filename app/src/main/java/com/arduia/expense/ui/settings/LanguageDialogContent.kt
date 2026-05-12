package com.arduia.expense.ui.settings

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.common.language.LanguageUiModel

@Composable
fun LanguageDialogContent(
    languages: List<LanguageUiModel> = emptyList(),
    isRestartEnabled: Boolean = false,
    onLanguageSelected: (LanguageUiModel) -> Unit = {},
    onRestartClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {}
) {
    ChooseLanguageDialogScreen(
        languages = languages,
        isRestartEnabled = isRestartEnabled,
        onLanguageSelected = onLanguageSelected,
        onRestartClick = onRestartClick,
        onCloseClick = onCloseClick,
        onSearchQueryChange = onSearchQueryChange
    )
}

@Preview(showBackground = true)
@Composable
private fun LanguageDialogContentPreview() {
    ProExpenseTheme {
        LanguageDialogContent(
            languages = listOf(
                LanguageUiModel(id = "en", flag = R.drawable.flag_united_states, name = "English", isSelectedVisible = View.VISIBLE),
                LanguageUiModel(id = "my", flag = R.drawable.flag_myanmar, name = "Myanmar (Burmese)")
            ),
            isRestartEnabled = true
        )
    }
}
