package com.arduia.expense.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ChooseLanguageScreen(
    languages: List<String> = emptyList(), // Replace with proper model later
    selectedLanguage: String = "",
    onLanguageSelected: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.choose_language),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProExpenseTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = stringResource(id = R.string.search), // Assuming string.search exists or use hint
                modifier = Modifier.fillMaxWidth()
            )
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
    }
}

@Composable
fun LanguageItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ProExpenseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Placeholder
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_done),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseLanguageScreenPreview() {
    ProExpenseTheme {
        ChooseLanguageScreen(
            languages = listOf("English", "Myanmar (Burmese)"),
            selectedLanguage = "English"
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ChooseLanguageScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ChooseLanguageScreen(
            languages = listOf("English", "Myanmar (Burmese)"),
            selectedLanguage = "English"
        )
    }
}
