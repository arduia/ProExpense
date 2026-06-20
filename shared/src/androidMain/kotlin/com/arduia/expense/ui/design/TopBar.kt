package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Preview(
    name = "ProTopBar — app bar title",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun ProTopBarPreview() {
    ProExpenseTheme {
        ProTopBar(
            title = "New expense",
            onBack = {},
            backLabel = "Back",
        )
    }
}

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
    backLabel: String? = null,
    action: ProTopBarAction = ProTopBarAction.None,
    onAction: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shapes = ProExpenseTheme.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space8),
        contentAlignment = Alignment.Center,
    ) {
        if (onBack != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .proClickable(
                        onClick = onBack,
                        shape = shapes.searchField,
                    )
                    .padding(horizontal = dimens.space8, vertical = dimens.space4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space4),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Back,
                    contentDescription = if (backLabel == null) "Back" else null,
                    tint = colors.onSurface,
                )
                if (backLabel != null) {
                    Text(
                        text = backLabel,
                        style = typography.bodyMedium,
                        color = colors.onSurface,
                    )
                }
            }
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = typography.appBarTitle,
                color = colors.onSurface,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        when (action) {
            ProTopBarAction.More -> ProIcon(
                glyph = ProIconGlyph.More,
                contentDescription = "More",
                tint = colors.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .proIconClickable(onClick = onAction),
            )
            ProTopBarAction.Close -> ProIcon(
                glyph = ProIconGlyph.Close,
                contentDescription = "Close",
                tint = colors.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .proIconClickable(onClick = onAction),
            )
            ProTopBarAction.None -> Unit
        }
    }
}
