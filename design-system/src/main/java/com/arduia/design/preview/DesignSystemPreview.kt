package com.arduia.design.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme

@Preview(showBackground = true)
@Composable
fun DesignSystemPreview() {
    ProExpenseTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Design System Preview",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            
            ProExpenseTextField(
                value = "",
                onValueChange = {},
                label = "Sample Text Field"
            )
            
            ProExpenseButton(
                text = "Primary Button",
                onClick = {}
            )
            
            ProExpenseButton(
                text = "Secondary Button",
                onClick = {},
                variant = com.arduia.design.components.ButtonVariant.SECONDARY
            )
            
            ProExpenseButton(
                text = "Outline Button",
                onClick = {},
                variant = com.arduia.design.components.ButtonVariant.OUTLINE
            )
            
            ProExpenseCard {
                Text(
                    text = "This is a sample card component",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
