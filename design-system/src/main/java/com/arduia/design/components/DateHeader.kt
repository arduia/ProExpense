package com.arduia.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun DateHeader(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // ?backgroundColor
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium, // Body1 in XML but small header feels better as label
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}


@Preview
@Composable
fun PreviewDateHeader() {
    ProExpenseTheme {
        DateHeader(date = "October 24, 2023")
    }
}
