package com.arduia.design.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.design.theme.ProExpenseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProExpenseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ProExpenseTextFieldPreview() {
    ProExpenseTheme(darkTheme = false) {
        ProExpenseTextField(
            value = "",
            onValueChange = {},
            label = "Label",
            placeholder = "Placeholder",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProExpenseTextFieldDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ProExpenseTextField(
            value = "Input Text",
            onValueChange = {},
            label = "Label",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Burmese", locale = "my", showBackground = true)
@Composable
fun ProExpenseTextFieldBurmesePreview() {
    ProExpenseTheme {
        ProExpenseTextField(
            value = "",
            onValueChange = {},
            label = "အမည်",
            placeholder = "Placeholder",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
