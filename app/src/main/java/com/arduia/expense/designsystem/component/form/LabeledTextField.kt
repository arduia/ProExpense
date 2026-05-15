package com.arduia.expense.designsystem.component.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    suffix: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            suffix = suffix?.let { { Text(text = it) } },
            isError = isError,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            enabled = enabled,
            shape = MaterialTheme.shapes.small,
        )
        if (isError && errorMessage != null) {
            Text(
                modifier = Modifier.padding(
                    start = ProExpenseTheme.spacing.grid2,
                    top = ProExpenseTheme.spacing.grid1,
                ),
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewLabeledTextFieldFilled() {
    ProExpenseTheme {
        LabeledTextField(
            label = "Amount",
            value = "100",
            onValueChange = {},
            suffix = "USD",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
    }
}

@Preview(name = "Empty")
@Composable
private fun PreviewLabeledTextFieldEmpty() {
    ProExpenseTheme {
        LabeledTextField(label = "Name", value = "", onValueChange = {}, placeholder = "e.g. Grocery")
    }
}

@Preview(name = "Error")
@Composable
private fun PreviewLabeledTextFieldError() {
    ProExpenseTheme {
        LabeledTextField(
            label = "Amount",
            value = "",
            onValueChange = {},
            isError = true,
            errorMessage = "Amount is required",
        )
    }
}

@Preview(name = "Disabled")
@Composable
private fun PreviewLabeledTextFieldDisabled() {
    ProExpenseTheme {
        LabeledTextField(label = "Date", value = "May 15, 2026", onValueChange = {}, enabled = false)
    }
}

@Preview(name = "Multiline")
@Composable
private fun PreviewLabeledTextFieldMultiline() {
    ProExpenseTheme {
        LabeledTextField(
            label = "Note",
            value = "Weekly grocery shopping\nat the supermarket",
            onValueChange = {},
            singleLine = false,
        )
    }
}
