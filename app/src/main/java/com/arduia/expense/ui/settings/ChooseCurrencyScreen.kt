package com.arduia.expense.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ChooseCurrencyScreen(
    currencies: List<String> = emptyList(), // Replace with proper model
    selectedCurrency: String = "",
    onCurrencySelected: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp) // padding from XML
        ) { // Elevation handle via shadow if needed, XML has elevation 3dp
            Text(
                text = stringResource(id = R.string.choose_currency),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProExpenseTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = stringResource(id = R.string.search), // Assuming string.search exists
                modifier = Modifier.fillMaxWidth()
            )
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(currencies) { currency ->
                CurrencyItem(
                    name = currency,
                    symbol = "$", // Mock symbol
                    isSelected = currency == selectedCurrency,
                    onClick = { onCurrencySelected(currency) }
                )
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
fun CurrencyItem(
    name: String,
    symbol: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ProExpenseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.2f)
            )
            
            // Vertical Divider (Guideline 0.25 in XML)
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
             Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.8f)
            )

            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_done),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseCurrencyScreenPreview() {
    ProExpenseTheme {
        ChooseCurrencyScreen(
            currencies = listOf("United State Dollar", "Myanmar Kyat"),
            selectedCurrency = "United State Dollar"
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ChooseCurrencyScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ChooseCurrencyScreen(
            currencies = listOf("United State Dollar", "Myanmar Kyat"),
            selectedCurrency = "United State Dollar"
        )
    }
}
