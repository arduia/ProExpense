package com.arduia.expense.ui.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class ProTopBarAction {
    None,
    More,
    Close,
}

@Composable
fun ProTopBar(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    action: ProTopBarAction = ProTopBarAction.None,
    onAction: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (onBack != null) {
            ProIcon(
                glyph = ProIconGlyph.Back,
                contentDescription = "Back",
                tint = colors.onSurface,
                modifier = Modifier.clickable(onClick = onBack),
            )
        } else {
            Spacer(modifier = Modifier.size(dimens.iconNav))
        }
        Text(
            text = title,
            style = typography.screenTitle,
            color = colors.onSurface,
            modifier = Modifier.weight(1f).padding(horizontal = dimens.space12),
        )
        when (action) {
            ProTopBarAction.More -> ProIcon(
                glyph = ProIconGlyph.More,
                contentDescription = "More",
                tint = colors.onSurface,
                modifier = Modifier.clickable(onClick = onAction),
            )
            ProTopBarAction.Close -> ProIcon(
                glyph = ProIconGlyph.Close,
                contentDescription = "Close",
                tint = colors.onSurface,
                modifier = Modifier.clickable(onClick = onAction),
            )
            ProTopBarAction.None -> Spacer(modifier = Modifier.size(dimens.iconNav))
        }
    }
}
