package com.arduia.expense.designsystem.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.designsystem.theme.ColorNegative
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews
import com.arduia.expense.designsystem.theme.White

@Composable
fun CircularBadge(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewCircularBadgeDefault() {
    ProExpenseTheme { CircularBadge(label = "F") }
}

@Preview(name = "Small 24dp")
@Composable
private fun PreviewCircularBadgeSmall() {
    ProExpenseTheme { CircularBadge(label = "T", size = 24.dp) }
}

@Preview(name = "Large 56dp")
@Composable
private fun PreviewCircularBadgeLarge() {
    ProExpenseTheme { CircularBadge(label = "S", size = 56.dp) }
}

@Preview(name = "Negative color")
@Composable
private fun PreviewCircularBadgeNegative() {
    ProExpenseTheme { CircularBadge(label = "H", backgroundColor = ColorNegative, contentColor = White) }
}
