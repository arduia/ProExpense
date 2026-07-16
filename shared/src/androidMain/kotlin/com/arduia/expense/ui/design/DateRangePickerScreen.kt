package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Event start/end date range: a full-screen month calendar in range-select mode with a Start/End
 * stub row above it, matching the Claude Design "Date & Time Picker — Dev Handoff" spec's
 * `DateRangeSheet`. Replaces two independent single-date pickers (no start<=end enforcement) with
 * one true range picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerScreen(
    visible: Boolean,
    title: String,
    initialStartEpochMillis: Long?,
    initialEndEpochMillis: Long?,
    onApply: (start: Long, end: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    // See DateTimePickerScreen for why this keys on the initial values, not `visible`.
    val rangeState =
        key(initialStartEpochMillis, initialEndEpochMillis) {
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = initialStartEpochMillis,
                initialSelectedEndDateMillis = initialEndEpochMillis,
            )
        }
    val start = rangeState.selectedStartDateMillis
    val end = rangeState.selectedEndDateMillis

    PickerScreenShell(
        visible = visible,
        title = title,
        onClose = onDismiss,
        footer = {
            ProButton(
                text =
                    when {
                        start != null && end != null ->
                            stringResource(
                                R.string.date_range_picker_use,
                                PlatformDateFormatter.shortDateLabel(start),
                                PlatformDateFormatter.shortDateLabel(end),
                            )
                        start != null -> stringResource(R.string.date_range_picker_pick_end)
                        else -> stringResource(R.string.date_range_picker_pick_start)
                    },
                onClick = {
                    if (start != null && end != null) {
                        onApply(start, end)
                        onDismiss()
                    }
                },
                enabled = start != null && end != null,
                size = ProButtonSize.Md,
                fillMaxWidth = true,
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimens.space16),
                horizontalArrangement = Arrangement.spacedBy(dimens.space10),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val selectPrompt = stringResource(R.string.date_range_picker_select)
                DateRangeStub(
                    label = stringResource(R.string.date_range_picker_start_label),
                    value = start?.let { PlatformDateFormatter.shortDateLabel(it) } ?: selectPrompt,
                    active = start == null,
                    onClick = { rangeState.setSelection(null, null) },
                    modifier = Modifier.weight(1f),
                )
                ProIcon(
                    glyph = ProIconGlyph.ChevronRight,
                    contentDescription = null,
                    tint = colors.onSurfaceMuted,
                )
                DateRangeStub(
                    label = stringResource(R.string.date_range_picker_end_label),
                    value = end?.let { PlatformDateFormatter.shortDateLabel(it) } ?: selectPrompt,
                    active = start != null && end == null,
                    onClick = null,
                    modifier = Modifier.weight(1f),
                )
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                MaterialTheme(typography = proDatePickerTypography()) {
                    DateRangePicker(
                        state = rangeState,
                        modifier = Modifier.fillMaxSize().clipToBounds(),
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = proDatePickerColors(),
                    )
                }
                // Same fading-edge trick as JournalDateRangeSheet: the next-month preview row
                // scrolls right up to the bottom of this box with near-zero natural gap.
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(dimens.space32)
                            .background(
                                Brush.verticalGradient(colors = listOf(Color.Transparent, colors.paper)),
                            ),
                )
            }
        }
    }
}

@Composable
private fun DateRangeStub(
    label: String,
    value: String,
    active: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Column(
        modifier =
            modifier
                .clip(shape)
                .background(if (active) colors.primaryTint else colors.surface)
                .border(1.4.dp, if (active) colors.primary else colors.lineStrong, shape)
                .then(if (onClick != null) Modifier.proClickable(onClick = onClick, shape = shape) else Modifier)
                .padding(horizontal = dimens.space12, vertical = dimens.space10),
    ) {
        Text(text = label.uppercase(), style = typography.eyebrow, color = colors.onSurfaceMuted)
        Text(text = value, style = typography.bodySemiBold, color = colors.onSurface)
    }
}

@Preview(
    name = "Date range picker — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DateRangePickerScreenEmptyPreview() {
    ProExpenseTheme {
        DateRangePickerScreen(
            visible = true,
            title = "Event dates",
            initialStartEpochMillis = null,
            initialEndEpochMillis = null,
            onApply = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Date range picker — range set",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DateRangePickerScreenSetPreview() {
    ProExpenseTheme {
        DateRangePickerScreen(
            visible = true,
            title = "Event dates",
            initialStartEpochMillis = 1_716_600_000_000L,
            initialEndEpochMillis = 1_717_200_000_000L,
            onApply = { _, _ -> },
            onDismiss = {},
        )
    }
}
