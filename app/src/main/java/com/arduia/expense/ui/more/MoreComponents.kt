package com.arduia.expense.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

// Shared horizontal inset for More-flow content, matching the profile wizard inset.
val MoreHorizontalPadding = 24.dp

/**
 * Top nav bar for More sub-screens: a `‹ More` back affordance on the left, a centered title, and
 * an optional trailing slot (e.g. the add-category tile). Mirrors design/screens/06-more specs.
 */
@Composable
fun MoreNavBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backLabel: String = "More",
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBack)
                .padding(4.dp)
                .semantics { contentDescription = "Back to $backLabel" },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            ChevronLeftGlyph(color = colors.ink, modifier = Modifier.size(18.dp))
            Text(
                text = backLabel,
                style = typography.body.copy(fontSize = 16.sp),
                color = colors.ink,
            )
        }
        Text(
            text = title,
            style = typography.bodyEmphasis.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
            color = colors.ink,
        )
        if (trailing != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                trailing()
            }
        }
    }
}

/** Mono uppercase group header used above settings/feature groups. */
@Composable
fun MoreSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = ProExpenseTheme.typography.eyebrow,
        color = ProExpenseTheme.colors.ink3,
    )
}

/** Rounded, tinted icon tile holding a stroke glyph. */
@Composable
fun IconTile(
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    glyph: @Composable (Modifier) -> Unit,
) {
    Surface(
        modifier = modifier.size(size),
        shape = ProExpenseTheme.shapes.buttonSmall,
        color = tint,
    ) {
        Box(contentAlignment = Alignment.Center) {
            glyph(Modifier.size(size * 0.55f))
        }
    }
}

/**
 * A grouped card of settings rows. Children are rendered inside a single card with hairline inner
 * dividers between rows.
 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    rows: List<@Composable () -> Unit>,
) {
    val colors = ProExpenseTheme.colors
    val shapes = ProExpenseTheme.shapes

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = shapes.card,
                clip = false,
                ambientColor = colors.ink.copy(alpha = 0.04f),
                spotColor = colors.ink.copy(alpha = 0.04f),
            ),
        shape = shapes.card,
        color = colors.card,
        contentColor = colors.ink,
        border = BorderStroke(1.dp, colors.line),
        tonalElevation = 0.dp,
    ) {
        Column {
            rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 60.dp),
                        thickness = 1.dp,
                        color = colors.line2,
                    )
                }
                row()
            }
        }
    }
}

/**
 * A single tappable settings row: tinted icon tile, label, optional trailing detail value, and a
 * chevron. Set [danger] to render the label/icon in the destructive red.
 */
@Composable
fun SettingsRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
    danger: Boolean = false,
    iconTint: Color = ProExpenseTheme.colors.paper,
    glyph: @Composable (Modifier) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val labelColor = if (danger) colors.danger else colors.ink

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(tint = iconTint, size = 32.dp, glyph = glyph)
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = ProExpenseTheme.typography.bodyEmphasis.copy(fontSize = 15.sp),
            color = labelColor,
        )
        if (detail != null) {
            Text(
                text = detail,
                style = ProExpenseTheme.typography.body,
                color = colors.muted,
            )
        }
        ChevronRightGlyph(color = colors.muted2, modifier = Modifier.size(18.dp))
    }
}

/** Group header + grouped card, used by the More hub. */
@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    rows: List<@Composable () -> Unit>,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        MoreSectionTitle(text = title, modifier = Modifier.padding(start = 4.dp))
        Spacer(modifier = Modifier.height(12.dp))
        SettingsCard(rows = rows)
    }
}
