package com.arduia.expense.ui.component.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class LanguageUiModel(
    val code: String,
    val nativeName: String,
    val englishName: String,
)

@Composable
fun LanguageRow(
    language: LanguageUiModel,
    selected: Boolean,
    onSelect: (LanguageUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(language) }
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = { onSelect(language) })
        Text(
            modifier = Modifier.weight(1f),
            text = language.nativeName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = language.englishName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun PreviewLanguageRow() {
    ProExpenseTheme {
        LanguageRow(
            language = LanguageUiModel(code = "en", nativeName = "English", englishName = "English"),
            selected = true,
            onSelect = {},
        )
    }
}
