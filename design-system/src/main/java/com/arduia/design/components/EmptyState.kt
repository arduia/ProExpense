package com.arduia.design.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.R
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun EmptyState(
    @StringRes message: Int,
    @DrawableRes icon: Int? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
            Text(
                text = stringResource(id = message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyState() {
    ProExpenseTheme {
        EmptyState(message = R.string.no_data, icon = R.drawable.ic_launcher_foreground)
    }
}
