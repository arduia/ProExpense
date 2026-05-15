package com.arduia.expense.designsystem.component.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
    )
}

@ThemePreviews
@Composable
private fun PreviewSearchBoxEmpty() {
    ProExpenseTheme {
        SearchBox(query = "", onQueryChange = {})
    }
}

@Preview(name = "With text")
@Composable
private fun PreviewSearchBoxFilled() {
    ProExpenseTheme {
        SearchBox(query = "Grocery", onQueryChange = {})
    }
}
