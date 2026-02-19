package com.arduia.design.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseShapes
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun ProExpenseCard(
    modifier: Modifier = Modifier,
    shape: Shape = ProExpenseShapes.extraSmall, // Default to small/square-ish as per legacy 0dp/2dp
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        content = content
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ProExpenseCardPreview() {
    ProExpenseTheme(darkTheme = false) {
        ProExpenseCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Card Title", style = MaterialTheme.typography.titleMedium)
                Text("Card Content", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProExpenseCardDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ProExpenseCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Card Title", style = MaterialTheme.typography.titleMedium)
                Text("Card Content", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(name = "Burmese", locale = "my", showBackground = true)
@Composable
fun ProExpenseCardBurmesePreview() {
    ProExpenseTheme {
        ProExpenseCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ခေါင်းစဉ်", style = MaterialTheme.typography.titleMedium)
                Text("အကြောင်းအရာ", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
