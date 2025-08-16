package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ButtonVariant
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun DesignSystemExampleScreen() {
    ProExpenseTheme {
        var textValue by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Design System Example",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            
            ProExpenseTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = "Enter some text",
                placeholder = "Type here..."
            )
            
            ProExpenseButton(
                text = "Primary Action",
                onClick = { /* Handle click */ }
            )
            
            ProExpenseButton(
                text = "Secondary Action",
                onClick = { /* Handle click */ },
                variant = ButtonVariant.SECONDARY
            )
            
            ProExpenseButton(
                text = "Outline Action",
                onClick = { /* Handle click */ },
                variant = ButtonVariant.OUTLINE
            )
            
            ProExpenseCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sample Card",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "This is an example of the ProExpenseCard component with some content inside it.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
