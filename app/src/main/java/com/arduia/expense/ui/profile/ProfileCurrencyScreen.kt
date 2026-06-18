package com.arduia.expense.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.design.ProLinearProgress
import com.arduia.expense.ui.design.ProRadio
import com.arduia.expense.ui.design.ProTextButton
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProExpenseSerif
import com.arduia.expense.ui.theme.ProExpenseTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Profile setup step 2 — home currency selection.
 * Mirrors `AndProfileCurrency` from android-onboarding.jsx.
 */
@Composable
fun ProfileCurrencyScreen(
    selectedCode: String,
    onCurrencySelected: (String) -> Unit,
    onStartTracking: () -> Unit,
    onShowAllCurrencies: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ProTopBar(
            title = "Home currency",
            trailing = {
                ProTextButton(
                    text = "All",
                    onClick = onShowAllCurrencies,
                    modifier = Modifier.padding(end = 8.dp),
                )
            },
        )
        ProLinearProgress(
            progress = 1f,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Text(
            text = "All entries default to this. You can still log in any currency per-expense.",
            style = typography.body,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            profileCurrencyOptions.forEach { currency ->
                CurrencyRow(
                    currency = currency,
                    selected = currency.code == selectedCode,
                    onClick = { onCurrencySelected(currency.code) },
                )
            }
        }

        ProFilledButton(
            text = "Start tracking",
            onClick = onStartTracking,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 22.dp),
        )
    }
}

@Composable
private fun CurrencyRow(
    currency: ProfileCurrencyOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.primaryContainer else colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (selected) colors.surface else colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.symbol,
                style = typography.currencyCode.copy(
                    fontFamily = ProExpenseSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = typography.currencyCode.fontSize * 1.125f,
                ),
                color = if (selected) colors.primaryDeep else colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
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
        ProRadio(selected = selected)
    }
}
