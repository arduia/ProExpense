package com.arduia.expense.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ChooseCurrencyDialogContent( // Renamed to avoid class conflict if any
    currencies: List<String> = emptyList(),
    selectedCurrency: String = "",
    onCurrencySelected: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.choose_currency),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drop_down),
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProExpenseTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = stringResource(id = R.string.search),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(currencies) { currency ->
                    CurrencyItem(
                        name = currency,
                        symbol = "$", // Mock
                        isSelected = currency == selectedCurrency,
                        onClick = { onCurrencySelected(currency) }
                    )
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProExpenseButton(
                text = stringResource(id = R.string.save),
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseCurrencyDialogContentPreview() {
    ProExpenseTheme {
        ChooseCurrencyDialogContent(
            currencies = listOf("United State Dollar", "Myanmar Kyat"),
            selectedCurrency = "United State Dollar"
        )
    }
}
