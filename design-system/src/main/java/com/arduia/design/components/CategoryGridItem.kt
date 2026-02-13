package com.arduia.design.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun CategoryGridItem(
    title: String,
    icon: Int, // Resource ID
    isSelected: Boolean,
    onCategorySelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Background color changes based on selection state
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    StandardCard(
        modifier = modifier
            .size(100.dp) // Example size
            .clickable(onClick = onCategorySelected),
        containerColor = backgroundColor,
        elevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            CategoryIcon(
                painter = painterResource(id = icon),
                backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                iconTint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                size = 40.dp,
                iconSize = 24.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryGridItem() {
    ProExpenseTheme {
        Row {
            CategoryGridItem(
                title = "Food",
                icon = R.drawable.ic_launcher_foreground,
                isSelected = false,
                onCategorySelected = {}
            )
            CategoryGridItem(
                title = "Transport",
                icon = R.drawable.ic_launcher_foreground,
                isSelected = true,
                onCategorySelected = {}
            )
        }
    }
}
