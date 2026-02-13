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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.ui.res.stringResource

@Composable
fun SelectionItem(
    title: String,
    icon: Int? = null,
    isSelected: Boolean,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    showDivider: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemSelected)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional Leading Icon (e.g. Flag for language, Checkbox for multi-select)
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconTint ?: MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 16.dp).size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check), // Ensure this resource exists or use Icons.Default.Check
                    contentDescription = stringResource(id = R.string.desc_selected),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (showDivider) {
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectionItem() {
    ProExpenseTheme {
        Column {
            SelectionItem(
                title = "English",
                icon = R.drawable.ic_launcher_foreground,
                isSelected = true,
                onItemSelected = {}
            )
            SelectionItem(
                title = "Myanmar",
                icon = R.drawable.ic_launcher_foreground,
                isSelected = false,
                onItemSelected = {}
            )
        }
    }
}
