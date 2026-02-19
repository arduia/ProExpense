package com.arduia.expense.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ChooseThemeDialogScreen(
    currentMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    onModeSelected: (Int) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val radioOptions = listOf(
        Triple(AppCompatDelegate.MODE_NIGHT_YES, R.string.dark_theme, "Dark Theme"),
        Triple(AppCompatDelegate.MODE_NIGHT_NO, R.string.light_theme, "Light Theme"),
        Triple(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.system_default, "System Default")
    )

    var selectedMode by remember { mutableIntStateOf(currentMode) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // Dialog surface
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.choose_theme), // "Choose Theme"
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(Modifier.selectableGroup()) {
                radioOptions.forEach { (mode, stringId, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (mode == selectedMode),
                                onClick = { selectedMode = mode },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (mode == selectedMode),
                            onClick = null, // null recommended for accessibility with selectable
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = stringResource(id = stringId),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
                TextButton(onClick = { onModeSelected(selectedMode) }) {
                    Text(stringResource(id = R.string.restart)) // "Restart" or "OK"
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseThemeDialogPreview() {
    ProExpenseTheme {
        ChooseThemeDialogScreen()
    }
}