package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Small squircle replica of the bottom-nav Add FAB, used as an inline affordance — e.g. the
 * home empty-state "or tap [+] below" hint that points at the real Add button. Mirrors the FAB's
 * primary fill and plus glyph at chip scale; tokenized via `addHintChip` shape/size.
 */
@Composable
fun AddHintChip(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Box(
        modifier =
            modifier
                .size(dimens.addHintChipSize)
                .clip(ProExpenseTheme.shapes.addHintChip)
                .background(colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Plus,
            contentDescription = null,
            tint = colors.onPrimaryWarm,
            size = dimens.addHintIconSize,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHintChipPreview() {
    ProExpenseTheme {
        AddHintChip()
    }
}
