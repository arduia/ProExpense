package com.arduia.design.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.ui.res.stringResource
import com.arduia.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProSearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(id = R.string.search),
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.desc_search)
        )
    }
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        shape = MaterialTheme.shapes.medium, // Uses grid_2 = 8.dp from Theme
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Preview
@Composable
fun PreviewProSearchBox() {
    ProExpenseTheme {
        ProSearchBox(query = "", onQueryChange = {})
    }
}
