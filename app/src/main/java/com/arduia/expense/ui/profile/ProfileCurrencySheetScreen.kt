package com.arduia.expense.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseSerif
import com.arduia.expense.ui.theme.ProExpenseTheme

private val SheetTitleStyle = TextStyle(
    fontFamily = ProExpenseSerif,
    fontSize = 22.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.01).em,
)

@Composable
fun ProfileCurrencySheetScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onCurrencySelect: (ProfileCurrency) -> Unit,
    modifier: Modifier = Modifier,
    currencies: List<ProfileCurrency> = ProfileCurrencies,
) {
    val colors = ProExpenseTheme.colors

    Box(modifier = modifier.fillMaxSize()) {
        // Dimmed step-2 screen behind the sheet.
        ProfileCurrencyScreen(
            selectedCode = "USD",
            onCurrencySelect = {},
            onSkip = {},
            onStartTracking = {},
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.scrim)
                .clickable(onClick = onClose)
                .semantics { contentDescription = "Close currency picker" },
        )

        CurrencySheet(
            query = query,
            onQueryChange = onQueryChange,
            onClose = onClose,
            onCurrencySelect = onCurrencySelect,
            currencies = currencies,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CurrencySheet(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onCurrencySelect: (ProfileCurrency) -> Unit,
    currencies: List<ProfileCurrency>,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.66f),
        shape = ProExpenseTheme.shapes.sheet,
        color = colors.card,
        contentColor = colors.ink,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ProfileHorizontalPadding),
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(ProExpenseTheme.motion.sheetHandleWidth)
                    .height(ProExpenseTheme.motion.sheetHandleHeight)
                    .clip(RoundedCornerShape(99.dp))
                    .background(colors.sheetHandle),
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "All currencies",
                    style = SheetTitleStyle,
                    color = colors.ink,
                    modifier = Modifier.weight(1f),
                )
                CloseGlyph(
                    color = colors.ink2,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onClose)
                        .semantics { contentDescription = "Close" },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            CurrencySearchField(query = query, onQueryChange = onQueryChange)
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                currencies.forEach { currency ->
                    CurrencyRow(
                        currency = currency,
                        selected = false,
                        onClick = { onCurrencySelect(currency) },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CurrencySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.searchField)
            .background(colors.card)
            .border(1.4.dp, colors.primary, ProExpenseTheme.shapes.searchField)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchGlyph(color = colors.primary, modifier = Modifier.size(20.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(
                    text = "Search currency…",
                    style = typography.body.copy(fontSize = 16.sp),
                    color = colors.muted,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = typography.body.copy(fontSize = 16.sp, color = colors.ink),
                cursorBrush = SolidColor(colors.primary),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileCurrencySheetScreenPreview() {
    var query by remember { mutableStateOf("") }
    ProExpenseTheme {
        ProExpensePaperBackground {
            ProfileCurrencySheetScreen(
                query = query,
                onQueryChange = { query = it },
                onClose = {},
                onCurrencySelect = {},
            )
        }
    }
}
