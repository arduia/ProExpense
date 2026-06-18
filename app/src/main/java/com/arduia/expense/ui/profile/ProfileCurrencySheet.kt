package com.arduia.expense.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.IconSearch
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseSerif
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Currency picker bottom sheet — mirrors `AndProfileCurrencySheet`.
 */
@Composable
fun ProfileCurrencySheet(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val filtered = profileCurrencyOptions.filter {
        searchQuery.isBlank() ||
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.name.contains(searchQuery, ignoreCase = true)
    }
    val scrimInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
                .background(Color.Black.copy(alpha = 0.32f)),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(520.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 14.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(colors.outlineVariant),
            )

            Text(
                text = "All currencies",
                style = typography.profileTitle.copy(fontSize = typography.profileTitle.fontSize * 0.75f),
                color = colors.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(colors.surfaceVariant)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconSearch(color = colors.onSurfaceVariant)
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = typography.fieldValue.copy(color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search currency…",
                                style = typography.fieldValue,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        inner()
                    },
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                filtered.forEach { currency ->
                    SheetCurrencyRow(
                        currency = currency,
                        onClick = { onCurrencySelected(currency.code) },
                    )
                }
            }
        }
    }
}

/** Full-screen host for the currency-sheet design artboard (dimmed base + sheet). */
@Composable
fun ProfileCurrencySheetPreviewHost(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Box(modifier = modifier.fillMaxSize().background(colors.surface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProTopBar(title = "Home currency")
            ProLinearProgress(
                progress = 1f,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Box(modifier = Modifier.weight(1f))
        }
        ProfileCurrencySheet(
            searchQuery = "",
            onSearchQueryChange = {},
            onCurrencySelected = {},
            onDismiss = {},
        )
    }
}

@Composable
private fun SheetCurrencyRow(
    currency: ProfileCurrencyOption,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencyCode.copy(
                    fontFamily = ProExpenseSerif,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Column {
            Text(
                text = currency.code,
                style = typography.currencyCode,
                color = colors.onSurface,
            )
            Text(
                text = currency.name,
                style = typography.currencyName,
                color = colors.onSurfaceVariant,
            )
        }
    }
}
