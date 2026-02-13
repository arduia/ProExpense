package com.arduia.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    size: Dp = 50.dp,
    iconSize: Dp = 30.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconTint
            )
        }
    }
}

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    size: Dp = 50.dp,
    iconSize: Dp = 30.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconTint
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryIcon() {
    ProExpenseTheme {
        CategoryIcon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            backgroundColor = Color.Cyan
        )
    }
}
