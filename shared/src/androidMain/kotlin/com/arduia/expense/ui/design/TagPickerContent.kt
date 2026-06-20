package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

data class TagLinkOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val kind: TagLinkKind,
)

enum class TagLinkKind {
    Event,
    Debt,
}

@Composable
fun TagPickerContent(
    events: List<TagLinkOption>,
    debts: List<TagLinkOption>,
    selectedId: String?,
    selectedKind: TagLinkKind?,
    onSelected: (TagLinkOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val eventsDisabled = selectedKind == TagLinkKind.Debt
    val debtsDisabled = selectedKind == TagLinkKind.Event

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Text(
            text = stringResource(R.string.link_to_subtitle),
            style = typography.caption,
            color = colors.muted,
        )
        TagPickerSection(
            title = stringResource(R.string.link_events_section),
            unavailableHint = null,
            disabled = eventsDisabled,
        ) {
            events.forEach { option ->
                TagLinkRow(
                    option = option,
                    selected = selectedId == option.id && selectedKind == TagLinkKind.Event,
                    enabled = !eventsDisabled,
                    onClick = { onSelected(option) },
                )
            }
        }
        TagPickerSection(
            title = stringResource(R.string.link_debts_section),
            unavailableHint = if (debtsDisabled) stringResource(R.string.link_debts_unavailable) else null,
            disabled = debtsDisabled,
        ) {
            debts.forEach { option ->
                TagLinkRow(
                    option = option,
                    selected = selectedId == option.id && selectedKind == TagLinkKind.Debt,
                    enabled = !debtsDisabled,
                    onClick = { onSelected(option) },
                )
            }
        }
    }
}

@Composable
private fun TagPickerSection(
    title: String,
    unavailableHint: String?,
    disabled: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val labelColor = if (disabled) colors.onSurfaceMuted else colors.muted

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            Text(text = title, style = typography.eyebrow, color = labelColor)
            if (unavailableHint != null) {
                Text(text = unavailableHint, style = typography.caption, color = labelColor)
            }
        }
        content()
    }
}

@Composable
private fun TagLinkRow(
    option: TagLinkOption,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField
    val iconGlyph = when (option.kind) {
        TagLinkKind.Event -> ProIconGlyph.FeatEvents
        TagLinkKind.Debt -> ProIconGlyph.FeatDebt
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f)
            .clip(shape)
            .border(
                BorderStroke(
                    width = dimens.buttonBorderWidth,
                    color = if (selected) colors.primary else colors.line,
                ),
                shape = shape,
            )
            .background(if (selected) colors.primaryTint.copy(alpha = 0.45f) else colors.surface)
            .then(
                if (enabled) {
                    Modifier.proClickable(onClick = onClick, shape = shape)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.quickAccessIconSize)
                .clip(ProExpenseTheme.shapes.tile)
                .background(if (selected) colors.surface else colors.primarySoft),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = iconGlyph,
                contentDescription = null,
                tint = if (selected) colors.primary else colors.primary,
                size = dimens.iconInline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = option.subtitle,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        if (selected) {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = null,
                tint = colors.primary,
            )
        }
    }
}

@Preview(widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, showBackground = true)
@Composable
private fun TagPickerContentPreview() {
    ProExpenseTheme {
        TagPickerContent(
            events = listOf(
                TagLinkOption("bali", "Bali Trip", "May 12 — May 26", TagLinkKind.Event),
                TagLinkOption("wedding", "John's Wedding", "Jun 04 — Jun 06", TagLinkKind.Event),
            ),
            debts = listOf(
                TagLinkOption("lent-john", "Lent · John", "$50", TagLinkKind.Debt),
                TagLinkOption("owe-sarah", "Owe · Sarah", "$30", TagLinkKind.Debt),
            ),
            selectedId = "bali",
            selectedKind = TagLinkKind.Event,
            onSelected = {},
            modifier = Modifier.padding(ProExpenseTheme.dimensions.screenPadding),
        )
    }
}
