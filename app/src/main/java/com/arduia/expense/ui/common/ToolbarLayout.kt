package com.arduia.expense.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarLayout() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // actionBarSize approx
        contentAlignment = Alignment.BottomCenter
    ) {
        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}
