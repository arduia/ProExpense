package com.arduia.expense.designsystem.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Switch
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews

@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    val clickModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Column {
        Row(
            modifier = clickModifier
                .fillMaxWidth()
                .padding(
                    horizontal = ProExpenseTheme.spacing.grid3,
                    vertical = ProExpenseTheme.spacing.grid3,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            trailing()
        }
        if (showDivider) {
            HorizontalDivider(color = ProExpenseTheme.colors.divider)
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewSettingsRowWithSubtitle() {
    ProExpenseTheme {
        SettingsRow(title = "Currency", subtitle = "USD – US Dollar", onClick = {})
    }
}

@Preview(name = "Title only")
@Composable
private fun PreviewSettingsRowTitleOnly() {
    ProExpenseTheme {
        SettingsRow(title = "Feedback", onClick = {})
    }
}

@Preview(name = "No divider")
@Composable
private fun PreviewSettingsRowNoDivider() {
    ProExpenseTheme {
        SettingsRow(title = "Privacy Policy", showDivider = false, onClick = {})
    }
}

@Preview(name = "With trailing switch")
@Composable
private fun PreviewSettingsRowTrailing() {
    ProExpenseTheme {
        SettingsRow(title = "Notifications", trailing = { Switch(checked = true, onCheckedChange = {}) })
    }
}
