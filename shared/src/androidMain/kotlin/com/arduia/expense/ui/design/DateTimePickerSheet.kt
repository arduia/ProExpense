package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.util.Calendar

private const val QUICK_DAYS_BEFORE = 3
private const val QUICK_DAYS_AFTER = 3
private const val HOURS_IN_HALF_DAY = 12
private const val MINUTES_IN_HOUR = 60
private const val NOON_HOUR_OF_DAY = 12

private fun startOfDay(epochMillis: Long): Long =
    Calendar
        .getInstance()
        .apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

private fun addDays(
    epochMillis: Long,
    days: Int,
): Long =
    Calendar
        .getInstance()
        .apply {
            timeInMillis = epochMillis
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis

private fun calendarAt(epochMillis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = epochMillis }

private fun quickDayWindow(todayStart: Long): List<Long> {
    val offsets = -QUICK_DAYS_BEFORE..QUICK_DAYS_AFTER
    return offsets.map { addDays(todayStart, it) }
}

private fun initialHour12(hourOfDay: Int): Int {
    val hour = hourOfDay % HOURS_IN_HALF_DAY
    return if (hour == 0) HOURS_IN_HALF_DAY else hour
}

private fun to24Hour(
    hour12: Int,
    isPm: Boolean,
): Int =
    when {
        isPm && hour12 != HOURS_IN_HALF_DAY -> hour12 + HOURS_IN_HALF_DAY
        !isPm && hour12 == HOURS_IN_HALF_DAY -> 0
        else -> hour12
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateTimePickerSheet(
    visible: Boolean,
    initialEpochMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    val initialCalendar = remember(initialEpochMillis) { calendarAt(initialEpochMillis) }
    val todayStart = remember { startOfDay(System.currentTimeMillis()) }
    val quickDays = remember(todayStart) { quickDayWindow(todayStart) }

    var selectedDayMillis by remember(initialEpochMillis) { mutableLongStateOf(startOfDay(initialEpochMillis)) }
    var hour12 by
        remember(initialEpochMillis) {
            mutableIntStateOf(initialHour12(initialCalendar.get(Calendar.HOUR_OF_DAY)))
        }
    var minute by remember(initialEpochMillis) { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var isPm by
        remember(initialEpochMillis) {
            mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY) >= NOON_HOUR_OF_DAY)
        }
    var showFallbackCalendar by remember { mutableStateOf(false) }

    val isFuture = selectedDayMillis > todayStart
    val isInQuickWindow = selectedDayMillis in quickDays

    ProBottomSheetHost(
        visible = visible,
        title = stringResource(R.string.date_time_sheet_title),
        onClose = onDismiss,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            ResolvedDateHeader(visible = !isInQuickWindow, selectedDayMillis = selectedDayMillis)

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                SectionEyebrow(stringResource(R.string.date_time_date_label))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                    verticalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    quickDays.forEach { dayMillis ->
                        FilterChip(
                            label = PlatformDateFormatter.dayLabel(dayMillis),
                            selected = dayMillis == selectedDayMillis,
                            onClick = { selectedDayMillis = dayMillis },
                        )
                    }
                    FilterChip(
                        label = stringResource(R.string.date_time_pick_another),
                        selected = !isInQuickWindow,
                        onClick = { showFallbackCalendar = true },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                SectionEyebrow(stringResource(R.string.date_time_time_label))
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(ProExpenseTheme.shapes.card)
                            .border(1.dp, colors.lineStrong, ProExpenseTheme.shapes.card)
                            .background(colors.surface)
                            .padding(vertical = dimens.space16),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space16, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val hourIncrementDescription = stringResource(R.string.date_time_hour_increment)
                    val hourDecrementDescription = stringResource(R.string.date_time_hour_decrement)
                    TimeSpinner(
                        valueLabel = hour12.toString(),
                        stateDescription = stringResource(R.string.date_time_hour_state, hour12),
                        increment =
                            SpinnerStep(hourIncrementDescription) {
                                hour12 = if (hour12 == HOURS_IN_HALF_DAY) 1 else hour12 + 1
                            },
                        decrement =
                            SpinnerStep(hourDecrementDescription) {
                                hour12 = if (hour12 == 1) HOURS_IN_HALF_DAY else hour12 - 1
                            },
                    )
                    Text(
                        text = stringResource(R.string.date_time_separator),
                        style = typography.detailsAmount,
                        color = colors.onSurfaceMuted,
                    )
                    val minuteIncrementDescription = stringResource(R.string.date_time_minute_increment)
                    val minuteDecrementDescription = stringResource(R.string.date_time_minute_decrement)
                    TimeSpinner(
                        valueLabel = minute.toString().padStart(2, '0'),
                        stateDescription = stringResource(R.string.date_time_minute_state, minute),
                        increment = SpinnerStep(minuteIncrementDescription) { minute = (minute + 1) % MINUTES_IN_HOUR },
                        decrement =
                            SpinnerStep(minuteDecrementDescription) {
                                minute = (minute + MINUTES_IN_HOUR - 1) % MINUTES_IN_HOUR
                            },
                    )
                }
            }

            SegmentedToggle(
                options = listOf(stringResource(R.string.date_time_am), stringResource(R.string.date_time_pm)),
                selectedIndex = if (isPm) 1 else 0,
                onSelected = { isPm = it == 1 },
            )

            FutureDateNotice(visible = isFuture)

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                ProButton(
                    text = stringResource(R.string.date_time_cancel),
                    onClick = onDismiss,
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Md,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )

                ProButton(
                    text = stringResource(R.string.date_time_apply),
                    onClick = {
                        val result =
                            Calendar
                                .getInstance()
                                .apply {
                                    timeInMillis = selectedDayMillis
                                    set(Calendar.HOUR_OF_DAY, to24Hour(hour12, isPm))
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 0)
                                }.timeInMillis
                        onConfirm(result)
                        onDismiss()
                    },
                    size = ProButtonSize.Md,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    if (showFallbackCalendar) {
        FallbackCalendarDialog(
            initialSelectedDayMillis = selectedDayMillis,
            onSelect = { selectedDayMillis = startOfDay(it) },
            onDismiss = { showFallbackCalendar = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FallbackCalendarDialog(
    initialSelectedDayMillis: Long,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDayMillis)

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.scrim)
                .padding(dimens.space24),
        contentAlignment = Alignment.Center,
    ) {
        Surface(shape = ProExpenseTheme.shapes.card, color = colors.surface) {
            Column(
                modifier = Modifier.padding(dimens.space8),
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                MaterialTheme(typography = proDatePickerTypography()) {
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = proDatePickerColors(),
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.space16, vertical = dimens.space8),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    ProButton(
                        text = stringResource(R.string.date_time_cancel),
                        onClick = onDismiss,
                        variant = ProButtonVariant.Secondary,
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                    ProButton(
                        text = stringResource(R.string.date_time_apply),
                        onClick = {
                            datePickerState.selectedDateMillis?.let(onSelect)
                            onDismiss()
                        },
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private class SpinnerStep(
    val contentDescription: String,
    val onClick: () -> Unit,
)

@Composable
private fun TimeSpinner(
    valueLabel: String,
    stateDescription: String,
    increment: SpinnerStep,
    decrement: SpinnerStep,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        SpinnerChevron(rotation = 180f, step = increment)
        Text(
            text = valueLabel,
            style = typography.detailsAmount,
            color = colors.onSurface,
            modifier =
                Modifier.semantics(mergeDescendants = true) {
                    this.stateDescription = stateDescription
                    liveRegion = LiveRegionMode.Polite
                },
        )
        SpinnerChevron(rotation = 0f, step = decrement)
    }
}

@Composable
private fun ResolvedDateHeader(
    visible: Boolean,
    selectedDayMillis: Long,
) {
    if (!visible) return
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Text(
        text =
            stringResource(
                R.string.date_time_selected_date,
                PlatformDateFormatter.shortDateLabel(selectedDayMillis, withYear = true),
            ),
        style = typography.captionMedium,
        color = colors.onSurfaceMuted,
    )
}

@Composable
private fun SectionEyebrow(text: String) {
    Text(
        text = text.uppercase(),
        style = ProExpenseTheme.typography.eyebrow,
        color = ProExpenseTheme.colors.onSurfaceMuted,
    )
}

@Composable
private fun FutureDateNotice(visible: Boolean) {
    if (!visible) return
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .background(colors.primaryTint)
                .padding(dimens.space12),
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Sparkle,
            contentDescription = null,
            tint = colors.primary,
            size = dimens.iconInline,
        )
        Text(
            text = stringResource(R.string.date_time_future_notice),
            style = typography.caption,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun SpinnerChevron(
    rotation: Float,
    step: SpinnerStep,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    ProIcon(
        glyph = ProIconGlyph.ChevronDown,
        contentDescription = step.contentDescription,
        tint = colors.onSurfaceVariant,
        modifier =
            Modifier
                .rotate(rotation)
                .size(dimens.touchTargetMin)
                .clip(ProExpenseTheme.shapes.tile)
                .proIconClickable(onClick = step.onClick),
    )
}

@Preview(
    name = "Date/time picker — open",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DateTimePickerSheetPreview() {
    ProExpenseTheme {
        // Fixed instant so the baseline is deterministic; falls outside the ±3-day quick window
        // relative to "now" at any real test-run time, exercising the out-of-window header state.
        DateTimePickerSheet(
            visible = true,
            initialEpochMillis = 1_716_600_000_000L,
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Date/time picker — today, future notice",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DateTimePickerSheetTodayPreview() {
    ProExpenseTheme {
        DateTimePickerSheet(
            visible = true,
            initialEpochMillis = addDays(System.currentTimeMillis(), 2),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
