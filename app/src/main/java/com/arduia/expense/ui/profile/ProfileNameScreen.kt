package com.arduia.expense.ui.profile

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
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.IconUser
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProOutlinedField
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Profile setup step 1 — display name.
 * Mirrors `AndProfileName` from android-onboarding.jsx.
 */
@Composable
fun ProfileNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ProTopBar(title = "Set up profile")
        ProLinearProgress(
            progress = 0.5f,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp),
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
                modifier = Modifier.padding(top = 10.dp),
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
                modifier = Modifier.padding(top = 30.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        ProFilledButton(
            text = "Continue",
            onClick = onContinue,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 22.dp),
        )
    }
}
