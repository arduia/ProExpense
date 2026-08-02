package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Flat (non-gradient) sub-screen header — back button + eyebrow/title stack + optional trailing
 * slot, directly on [ProColors.paper]. Matches the Blue Banking canvas's `VBHeader flat` variant,
 * used by every pushed screen that isn't one of the four bottom-nav tabs (which use
 * [ProGradientHeader] instead).
 */
@Composable
fun ProFlatHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    // Screen-reader-only by default (icon-only per the canvas). Callers that route "back" to a
    // context-specific destination (a deep-linked Journal/Home entry point) can override this to
    // announce that destination — see SharedCostsSummaryScreen — since the icon alone can't.
    backContentDescription: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.space12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    // Background/border/ripple share one node, in that paint order, so the
                    // ripple renders on top of the tile fill instead of being hidden underneath
                    // a separate opaque child Box (the previous nesting only let the ripple show
                    // through the ~6dp margin outside the tile, not across its visible surface).
                    modifier =
                        Modifier
                            .minimumInteractiveComponentSize()
                            .size(dimens.buttonSmallHeight)
                            .clip(ProExpenseTheme.shapes.tile)
                            .background(colors.surface)
                            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.tile)
                            .proRippleClickable(onClick = onBack, role = Role.Button),
                    contentAlignment = Alignment.Center,
                ) {
                    ProIcon(
                        glyph = ProIconGlyph.Back,
                        contentDescription = backContentDescription ?: stringResource(R.string.back),
                        tint = colors.onSurfaceVariant,
                        size = dimens.iconInline,
                    )
                }
                Column {
                    if (eyebrow != null) {
                        Text(text = eyebrow, style = typography.eyebrow, color = colors.onSurfaceMuted)
                    }
                    Text(text = title, style = typography.sheetTitle, color = colors.onSurface)
                }
            }
            trailing?.invoke()
        }
        content?.invoke()
    }
}

@Preview(
    name = "ProFlatHeader",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun ProFlatHeaderPreview() {
    ProExpenseTheme {
        ProFlatHeader(
            title = "Debt",
            eyebrow = "Loans & IOUs",
            onBack = {},
        )
    }
}

@Preview(
    name = "ProFlatHeader — dark",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun ProFlatHeaderDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ProFlatHeader(
            title = "Debt",
            eyebrow = "Loans & IOUs",
            onBack = {},
        )
    }
}
