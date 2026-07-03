package com.arduia.expense.feature.history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.FilterChip
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.proDatePickerColors
import com.arduia.expense.ui.design.proDatePickerTypography
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDateRangeSheet(
    visible: Boolean,
    initialStartEpochMillis: Long?,
    initialEndEpochMillis: Long?,
    onConfirm: (start: Long, end: Long) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val dimens = ProExpenseTheme.dimensions

    // ProBottomSheetHost must stay composed while `visible` flips to false so its own
    // AnimatedVisibility can play the sheet's exit animation — an early `if (!visible) return`
    // here would remove it immediately instead, producing a hard cut. `key(visible)` re-seeds
    // the picker's selection from the current initial values each time the sheet opens.
    ProBottomSheetHost(
        visible = visible,
        title = stringResource(R.string.journal_date_range_title),
        onClose = onDismiss,
    ) {
        key(visible) {
            val rangeState = rememberDateRangePickerState(
                initialSelectedStartDateMillis = initialStartEpochMillis,
                initialSelectedEndDateMillis = initialEndEpochMillis,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = dimens.screenPadding)
                        .padding(bottom = dimens.space12),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space6),
                ) {
                    val today = todayUtcRange()
                    val thisWeek = thisWeekUtcRange()
                    val thisMonth = thisMonthUtcRange()
                    FilterChip(
                        label = stringResource(R.string.journal_date_range_preset_today),
                        selected = false,
                        onClick = {
                            onConfirm(today.first, today.second)
                            onDismiss()
                        },
                    )
                    FilterChip(
                        label = stringResource(R.string.journal_date_range_preset_week),
                        selected = false,
                        onClick = {
                            onConfirm(thisWeek.first, thisWeek.second)
                            onDismiss()
                        },
                    )
                    FilterChip(
                        label = stringResource(R.string.journal_date_range_preset_month),
                        selected = false,
                        onClick = {
                            onConfirm(thisMonth.first, thisMonth.second)
                            onDismiss()
                        },
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    MaterialTheme(typography = proDatePickerTypography()) {
                        DateRangePicker(
                            state = rangeState,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds(),
                            title = null,
                            headline = null,
                            // The text-input mode toggle reserves a header row even with title/headline
                            // hidden, leaving a large blank gap above the calendar grid. Not needed for
                            // a simple range filter — presets above already cover the fast path.
                            showModeToggle = false,
                            colors = proDatePickerColors(),
                        )
                    }
                    // The month list's next-month preview (e.g. June's "1" badge) scrolls right up
                    // to the bottom of this tightly-constrained box with near-zero natural gap —
                    // clipToBounds alone slices straight through that row instead of hiding it, which
                    // reads as broken. Fade it out instead of cutting it off.
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(dimens.space32)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, ProExpenseTheme.colors.surface),
                                ),
                            ),
                    )
                }

                HorizontalDivider(color = ProExpenseTheme.colors.lineSoft)

                Text(
                    text = stringResource(R.string.journal_date_range_hint),
                    style = ProExpenseTheme.typography.caption,
                    color = ProExpenseTheme.colors.onSurfaceMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding)
                        .padding(top = dimens.space12, bottom = dimens.space8),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding)
                        .padding(bottom = dimens.space24),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    ProButton(
                        text = stringResource(R.string.journal_date_range_clear),
                        onClick = {
                            onClear()
                            onDismiss()
                        },
                        variant = ProButtonVariant.Secondary,
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                    ProButton(
                        text = stringResource(R.string.journal_date_range_apply),
                        onClick = {
                            val start = rangeState.selectedStartDateMillis
                            val end = rangeState.selectedEndDateMillis
                            if (start != null && end != null) {
                                onConfirm(start, end)
                                onDismiss()
                            }
                        },
                        enabled = rangeState.selectedStartDateMillis != null && rangeState.selectedEndDateMillis != null,
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun utcMidnight(year: Int, month: Int, dayOfMonth: Int): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(year, month, dayOfMonth, 0, 0, 0)
    return utc.timeInMillis
}

private fun Calendar.toUtcMidnight(): Long = utcMidnight(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))

// Presets read the device-local calendar day/week/month (what the user actually experiences
// as "today"), then re-express those same Y/M/D fields as UTC-midnight instants — matching
// the semantics DateRangePickerState and the Journal filter already use everywhere else.
private fun todayUtcRange(): Pair<Long, Long> {
    val today = Calendar.getInstance().toUtcMidnight()
    return today to today
}

private fun thisWeekUtcRange(): Pair<Long, Long> {
    val now = Calendar.getInstance()
    val start = (now.clone() as Calendar).apply {
        val offset = (get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7
        add(Calendar.DAY_OF_YEAR, -offset)
    }
    val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 6) }
    return start.toUtcMidnight() to end.toUtcMidnight()
}

private fun thisMonthUtcRange(): Pair<Long, Long> {
    val now = Calendar.getInstance()
    val start = (now.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val end = (start.clone() as Calendar).apply {
        add(Calendar.MONTH, 1)
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return start.toUtcMidnight() to end.toUtcMidnight()
}

@Preview(
    name = "Journal — date range picker",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalDateRangeSheetPreview() {
    ProExpenseTheme {
        // Fixed instants keep the screenshot baseline deterministic.
        JournalDateRangeSheet(
            visible = true,
            initialStartEpochMillis = 1_716_600_000_000L,
            initialEndEpochMillis = 1_717_200_000_000L,
            onConfirm = { _, _ -> },
            onClear = {},
            onDismiss = {},
        )
    }
}
