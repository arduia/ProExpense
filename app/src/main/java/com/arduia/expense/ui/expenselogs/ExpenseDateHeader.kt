package com.arduia.expense.ui.expenselogs

import android.content.res.Configuration
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
fun ExpenseDateHeader(
    date: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Use background color
            .padding(vertical = 8.dp, horizontal = 20.dp) // Match XML margins
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseDateHeaderPreview() {
    ProExpenseTheme {
        ExpenseDateHeader(date = "November 1 2020")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseDateHeaderDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ExpenseDateHeader(date = "November 1 2020")
    }
}
