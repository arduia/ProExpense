package com.arduia.expense.ui.settings

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseSearchField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.common.language.LanguageUiModel

// grid_3 = 16dp, grid_2 = 8dp
private val GRID_3 = 16.dp
private val GRID_2 = 8.dp

/**
 * Bottom sheet dialog content for choosing a language.
 * Matches fragment_choose_language_dialog.xml layout and behavior.
 */
@Composable
fun ChooseLanguageDialogScreen(
    languages: List<LanguageUiModel> = emptyList(),
    isRestartEnabled: Boolean = false,
    onLanguageSelected: (LanguageUiModel) -> Unit = {},
    onRestartClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(GRID_3),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Header row: title + close icon — matches imv_drop_close + tv_choose_language
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
                        contentDescription = stringResource(id = R.string.close),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(GRID_3))

            // Search box — matches search_box (MaterialSearchBox) in XML
            ProExpenseSearchField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                hint = stringResource(id = R.string.search),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(GRID_3))

            // Language list — 250dp fixed height matching rv_languages in XML
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(languages) { model ->
                    LanguageItem(
                        model = model,
                        onClick = { onLanguageSelected(model) },
                        modifier = Modifier.padding(bottom = GRID_2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(GRID_3))

            // Restart button — matches btn_restart, enabled state driven by ViewModel
            ProExpenseButton(
                text = stringResource(id = R.string.restart),
                onClick = onRestartClick,
                enabled = isRestartEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseLanguageDialogScreenPreview() {
    ProExpenseTheme {
        ChooseLanguageDialogScreen(
            languages = listOf(
                LanguageUiModel(id = "en", flag = R.drawable.flag_united_states, name = "English", isSelectedVisible = View.VISIBLE),
                LanguageUiModel(id = "my", flag = R.drawable.flag_myanmar, name = "Myanmar (Burmese)")
            ),
            isRestartEnabled = true
        )
    }
}
