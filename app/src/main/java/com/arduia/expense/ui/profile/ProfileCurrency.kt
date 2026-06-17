package com.arduia.expense.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseSerif
import com.arduia.expense.ui.theme.ProExpenseTheme

data class ProfileCurrency(
    val code: String,
    val name: String,
    val symbol: String,
)

// Local short list for the first-run wizard — see design spec 02-prof-currency.
val ProfileCurrencies: List<ProfileCurrency> = listOf(
    ProfileCurrency("USD", "US Dollar", "$"),
    ProfileCurrency("EUR", "Euro", "€"),
    ProfileCurrency("GBP", "British Pound", "£"),
    ProfileCurrency("JPY", "Japanese Yen", "¥"),
    ProfileCurrency("INR", "Indian Rupee", "₹"),
    ProfileCurrency("AED", "UAE Dirham", "د.إ"),
)

private val CurrencyCodeStyle = TextStyle(
    fontFamily = ProExpenseSerif,
    fontSize = 20.sp,
    lineHeight = 22.sp,
)

@Composable
fun CurrencySymbolChip(
    symbol: String,
    background: Color,
    glyphColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = background,
        contentColor = glyphColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                style = CurrencyCodeStyle,
                color = glyphColor,
            )
        }
    }
}

@Composable
fun CurrencyRow(
    currency: ProfileCurrency,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    val background = if (selected) colors.primaryTint else colors.card
    val border = if (selected) {
        BorderStroke(1.4.dp, colors.primary)
    } else {
        BorderStroke(1.dp, colors.line)
    }
    val chipBackground = if (selected) colors.card else ProExpenseTheme.colors.paper
    val chipGlyph = if (selected) colors.primaryDeep else colors.ink2

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.selected = selected
                contentDescription = "${currency.code} ${currency.name}"
            },
        shape = ProExpenseTheme.shapes.quickTile,
        color = background,
        contentColor = colors.ink,
        border = border,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CurrencySymbolChip(
                symbol = currency.symbol,
                background = chipBackground,
                glyphColor = chipGlyph,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currency.code,
                    style = ProExpenseTheme.typography.bodyEmphasis.copy(fontSize = 16.sp),
                    color = colors.ink,
                )
                Text(
                    text = currency.name,
                    style = ProExpenseTheme.typography.caption,
                    color = colors.muted,
                )
            }
            if (selected) {
                CheckGlyph(
                    color = colors.primaryDeep,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
