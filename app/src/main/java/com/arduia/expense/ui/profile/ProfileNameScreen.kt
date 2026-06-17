package com.arduia.expense.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.design.ProExpenseButton
import com.arduia.expense.ui.design.ProExpenseButtonSize
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onSkip: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxSize()) {
        WizHeader(step = 1, total = 2, onSkip = onSkip)

        WizTitle(
            kicker = "PROFILE · 1 OF 2",
            title = "Set up your profile",
            subtitle = "Your name personalizes the app — greeting you on the home screen and " +
                "identifying your records and exports. No account needed; everything stays on your device.",
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(modifier = Modifier.padding(horizontal = ProfileHorizontalPadding)) {
            Text(
                text = "PROFILE NAME",
                style = typography.eyebrow,
                color = colors.primaryDeep,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileNameField(
                value = name,
                onValueChange = onNameChange,
                onImeDone = onContinue,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Used on your home screen and CSV exports.",
                style = typography.caption.copy(fontSize = 12.sp),
                color = colors.muted,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ProExpenseButton(
            text = "Continue",
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ProfileHorizontalPadding)
                .padding(bottom = 38.dp),
            size = ProExpenseButtonSize.Large,
        )
    }
}

@Composable
private fun ProfileNameField(
    value: String,
    onValueChange: (String) -> Unit,
    onImeDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    // Focus ring: 4px blue-100 frame around a 14px blue-500 outlined card (design spec).
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.primaryTint)
            .padding(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.searchField)
                .background(colors.card)
                .border(1.4.dp, colors.primary, ProExpenseTheme.shapes.searchField)
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserGlyph(
                color = colors.primaryDeep,
                modifier = Modifier.size(20.dp),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = "e.g. Maya",
                        style = typography.body.copy(fontSize = 17.sp, lineHeight = 22.sp),
                        color = colors.muted,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = typography.body.copy(fontSize = 17.sp, lineHeight = 22.sp, color = colors.ink),
                    cursorBrush = SolidColor(colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onImeDone() }),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileNameScreenPreview() {
    var name by remember { mutableStateOf("Maya") }
    ProExpenseTheme {
        ProExpensePaperBackground {
            ProfileNameScreen(
                name = name,
                onNameChange = { name = it },
                onSkip = {},
                onContinue = {},
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileNameScreenEmptyPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            ProfileNameScreen(
                name = "",
                onNameChange = {},
                onSkip = {},
                onContinue = {},
            )
        }
    }
}
