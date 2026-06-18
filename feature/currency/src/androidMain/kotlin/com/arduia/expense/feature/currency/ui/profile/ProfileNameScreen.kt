package com.arduia.expense.feature.currency.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.IconUser
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProOutlinedField
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ProTopBar(title = "Set up profile")
        ProLinearProgress(
            progress = 0.5f,
            modifier = Modifier.padding(horizontal = dims.space16),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space24)
                .padding(top = dims.space28),
        ) {
            Text(
                text = "What should we call you?",
                style = typography.profileTitle,
                color = colors.onSurface,
            )
            Text(
                text = "Your name personalizes the app and identifies your records and exports. " +
                    "No account needed — everything stays on your device.",
                style = typography.body,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = dims.space10),
            )

            ProOutlinedField(
                label = "Profile name",
                value = name,
                onValueChange = onNameChange,
                placeholder = "e.g. Maya",
                helper = "Used on your home screen and CSV exports.",
                leading = { IconUser(color = colors.primaryDeep) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onContinue() }),
                modifier = Modifier.padding(top = dims.space30),
            )
        }

        Spacer(Modifier.weight(1f))

        ProFilledButton(
            text = "Continue",
            onClick = onContinue,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .padding(horizontal = dims.space20)
                .padding(bottom = dims.space22),
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileNameScreenEmptyPreview() {
    ProExpenseTheme {
        ProfileNameScreen(name = "", onNameChange = {}, onContinue = {})
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileNameScreenFilledPreview() {
    ProExpenseTheme {
        ProfileNameScreen(name = "Maya", onNameChange = {}, onContinue = {})
    }
}
