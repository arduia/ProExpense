package com.arduia.design.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun DesignSystemShowcase() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Typography", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        
        Text("Title Large - 22sp", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium - 18sp (MediumTitle)", style = MaterialTheme.typography.titleMedium)
        Text("Title Small (Burmese) - မင်္ဂလာပါ", style = MaterialTheme.typography.titleSmall)
        Text("Body Large - 16sp", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium - 14sp", style = MaterialTheme.typography.bodyMedium)

        Text("Buttons", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        
        ProExpenseButton(
            onClick = {},
            text = "Primary Button",
            modifier = Modifier.fillMaxWidth()
        )
        
        ProExpenseButton(
            onClick = {},
            text = "Disabled Button",
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Inputs", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)

        ProExpenseTextField(
            value = "",
            onValueChange = {},
            label = "Label",
            placeholder = "Placeholder",
            modifier = Modifier.fillMaxWidth()
        )
        
        Text("Cards", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        
        ProExpenseCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Card Content Title", style = MaterialTheme.typography.titleMedium)
                Text("Card Content Body", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ProExpenseTheme(darkTheme = false) {
        DesignSystemShowcase()
    }
}

@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ProExpenseTheme(darkTheme = true) {
        DesignSystemShowcase()
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun BurmesePreview() {
    ProExpenseTheme(darkTheme = false) {
        DesignSystemShowcase()
    }
}
