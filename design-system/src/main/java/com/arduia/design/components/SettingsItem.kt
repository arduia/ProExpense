package com.arduia.design.components
    
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

import com.arduia.design.theme.divider_color

@Composable
fun SettingsItem(
    title: String,
    value: String? = null,
    icon: Int? = null, // Leading icon
    showChevron: Boolean = false,
    onClick: () -> Unit,
    showDivider: Boolean = true,
    trailingElement: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 16.dp).size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }

            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            if (trailingElement != null) {
                trailingElement()
            } else if (showChevron) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_drop_down), // Reuse drop down or specific right arrow
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp) // Specific size for chevron
                )
            }
        }
        
        if (showDivider) {
            Divider(
                color = divider_color,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsItem() {
    ProExpenseTheme {
        Column {
            SettingsItem(
                title = "Theme Mode",
                value = "Dark",
                onClick = {},
                icon = R.drawable.ic_theme
            )
            SettingsItem(
                title = "Privacy and Policy",
                showChevron = true,
                onClick = {}
            )
            SettingsItem(
                title = "App Version",
                value = "1.0.0",
                showDivider = false,
                onClick = {}
            )
        }
    }
}
