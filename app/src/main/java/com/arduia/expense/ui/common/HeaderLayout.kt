package com.arduia.expense.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun HeaderLayout(
    onCloseClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // App Logo
        Column {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Beta Badge
                Text(
                    text = stringResource(id = R.string.beta),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White, // ProExpense Warning Text Color? XML says ?colorOnWarning which usually is white on warning bg
                    modifier = Modifier
                        .background(Color(0xFFFF9800), shape = MaterialTheme.shapes.small) // Warning Color
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // Close Button
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_left),
                contentDescription = "Close",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderLayoutPreview() {
    ProExpenseTheme {
        HeaderLayout()
    }
}
