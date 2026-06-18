package com.arduia.expense.feature.currency.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProOutlinedField
import com.arduia.expense.ui.design.ProfileEyebrow
import com.arduia.expense.ui.design.ProfileSetupHeader
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        ProfileSetupHeader(step = 1, totalSteps = 2, onSkip = onSkip)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space24)
                .padding(top = dims.space8),
        ) {
            ProfileEyebrow(step = 1, totalSteps = 2)
            Text(
                text = "Set up your profile",
                style = typography.screenTitle,
                color = colors.onSurface,
                modifier = Modifier.padding(top = dims.space12),
            )
            Text(
                text = "Your name personalizes the app and identifies your records and exports. " +
                    "No account needed — everything stays on your device.",
                style = typography.subtitle,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dims.space12),
            )

            ProOutlinedField(
                label = "Profile name",
                value = name,
                onValueChange = onNameChange,
                placeholder = "e.g. Maya",
                helper = "Used on your home screen and CSV exports.",
                leading = { IconUser(color = colors.onSurfaceMuted) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (name.isNotBlank()) onContinue() }),
                modifier = Modifier.padding(top = dims.space28),
            )
        }

        Spacer(Modifier.weight(1f))

        ProButton(
            text = "Continue",
            onClick = onContinue,
            enabled = name.isNotBlank(),
            fillMaxWidth = true,
            modifier = Modifier
                .padding(horizontal = dims.space20)
                .padding(bottom = dims.space22),
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileNameScreenPreview() {
    ProExpenseTheme {
        ProfileNameScreen(
            name = "Maya",
            onNameChange = {},
            onContinue = {},
            onSkip = {},
        )
    }
}
