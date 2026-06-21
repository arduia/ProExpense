package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Add,
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space8),
        contentAlignment = Alignment.Center,
    ) {
        if (onBack != null) {
            if (backLabel != null) {
                ProTextAction(
                    text = backLabel,
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart),
                    style = typography.bodyMedium,
                    color = colors.onSurface,
                    leading = {
                        ProIcon(
                            glyph = ProIconGlyph.Back,
                            contentDescription = null,
                            tint = colors.onSurface,
                        )
                    },
                )
            } else {
                ProIcon(
                    glyph = ProIconGlyph.Back,
                    contentDescription = "Back",
                    tint = colors.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .proIconClickable(onClick = onBack),
                )
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
            ProTopBarAction.Add -> Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(dimens.touchTargetMin)
                    .clip(ProExpenseTheme.shapes.tile)
                    .background(colors.primary)
                    .proIconClickable(onClick = onAction),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Plus,
                    contentDescription = "Add",
                    tint = colors.onPrimaryWarm,
                    size = dimens.iconNav,
                )
            }
            ProTopBarAction.None -> Unit
        }
    }
}
