package com.arduia.design.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        content = {
            content()
        }
    )
}


@Preview
@Composable
fun PreviewStandardCard() {
    ProExpenseTheme {
        StandardCard(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Simple Card Content",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
