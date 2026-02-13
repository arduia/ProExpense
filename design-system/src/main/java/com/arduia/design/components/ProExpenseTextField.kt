package com.arduia.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.foundation.shape.RoundedCornerShape
import com.arduia.design.theme.statistic_progress_blue

@Composable
fun ProExpenseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = placeholder?.let { { Text(text = it) } },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = RoundedCornerShape(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = statistic_progress_blue,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = statistic_progress_blue,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            cursorColor = statistic_progress_blue
        )
    )
    
    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProExpenseTextField() {
    ProExpenseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProExpenseTextField(value = "", onValueChange = {}, label = "Label")
            ProExpenseTextField(value = "Input text", onValueChange = {}, label = "With value")
            ProExpenseTextField(
                value = "Error text",
                onValueChange = {},
                label = "With error",
                isError = true,
                errorMessage = "This is an error message"
            )
        }
    }
}
