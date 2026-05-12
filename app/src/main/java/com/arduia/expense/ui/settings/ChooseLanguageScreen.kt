package com.arduia.expense.ui.settings

import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseSearchField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.common.language.LanguageUiModel

// grid_3 = 16dp, grid_2 = 8dp (from Shape.kt comment)
private val GRID_3 = 16.dp
private val GRID_2 = 8.dp

@Composable
fun ChooseLanguageScreen(
    languages: List<LanguageUiModel> = emptyList(),
    onLanguageSelected: (LanguageUiModel) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search header — ?colorSurface background matching XML linear_search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(GRID_3)
        ) {
            Text(
                text = stringResource(id = R.string.choose_language),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = GRID_3)
            )

            ProExpenseSearchField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                hint = stringResource(id = R.string.search),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Language list — ?backgroundColor matching XML rv_languages
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = GRID_3)
                .padding(bottom = GRID_3)
        ) {
            items(languages) { model ->
                LanguageItem(
                    model = model,
                    onClick = { onLanguageSelected(model) },
                    modifier = Modifier.padding(top = GRID_2)
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    model: LanguageUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProExpenseCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GRID_3, vertical = GRID_3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag — 25dp x 25dp matching item_language.xml imv_flag
            Image(
                painter = painterResource(id = model.flag),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(GRID_3))

            Text(
                text = model.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Check icon — 30dp x 30dp, tinted primary (= blue_light_500) matching XML imv_checked
            if (model.isSelectedVisible == View.VISIBLE) {
                Image(
                    painter = painterResource(id = R.drawable.ic_done),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            } else {
                Spacer(modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseLanguageScreenPreview() {
    ProExpenseTheme {
        ChooseLanguageScreen(
            languages = listOf(
                LanguageUiModel(id = "en", flag = R.drawable.flag_united_states, name = "English", isSelectedVisible = View.VISIBLE),
                LanguageUiModel(id = "my", flag = R.drawable.flag_myanmar, name = "Myanmar (Burmese)")
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ChooseLanguageScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ChooseLanguageScreen(
            languages = listOf(
                LanguageUiModel(id = "en", flag = R.drawable.flag_united_states, name = "English", isSelectedVisible = View.VISIBLE),
                LanguageUiModel(id = "my", flag = R.drawable.flag_myanmar, name = "Myanmar (Burmese)")
            )
        )
    }
}
