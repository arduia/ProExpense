package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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

private const val HOURS_IN_HALF_DAY = 12
private const val MINUTES_IN_HOUR = 60
private const val NOON_HOUR_OF_DAY = 12

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

/**
 * Transaction date + time: a full-screen month calendar (single selection) plus an hour/minute
 * spinner and AM/PM toggle below it, matching the Claude Design "Date & Time Picker — Dev
 * Handoff" spec's `DateTimeSheet`. Replaces the earlier quick-pick-chip version.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerScreen(
    visible: Boolean,
    initialEpochMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    // key(initialEpochMillis), not key(visible): PickerScreenShell keeps this composed during its
    // exit animation (AnimatedVisibility), so keying on `visible` would tear the picker down mid
    // -animation. Keying on the initial value instead reseeds only when the caller passes a truly
    // different record, matching rememberDatePickerState's own "first composition wins" semantics.
    val datePickerState =
        key(initialEpochMillis) {
            rememberDatePickerState(initialSelectedDateMillis = startOfDay(initialEpochMillis))
        }
    val initialCalendar = remember(initialEpochMillis) { calendarAt(initialEpochMillis) }
    var hour12 by
        remember(initialEpochMillis) {
            mutableIntStateOf(initialHour12(initialCalendar.get(Calendar.HOUR_OF_DAY)))
        }
    var minute by remember(initialEpochMillis) { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var isPm by
        remember(initialEpochMillis) {
            mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY) >= NOON_HOUR_OF_DAY)
        }

    val todayStart = remember { startOfDay(System.currentTimeMillis()) }
    val selectedDayMillis = datePickerState.selectedDateMillis ?: startOfDay(initialEpochMillis)
    val isFuture = selectedDayMillis > todayStart

    PickerScreenShell(
        visible = visible,
        title = stringResource(R.string.date_time_sheet_title),
        onClose = onDismiss,
        footer = {
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
            )
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                SectionEyebrow(stringResource(R.string.date_time_date_label))
                MaterialTheme(typography = proDatePickerTypography()) {
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = proDatePickerColors(),
                        modifier = Modifier.fillMaxWidth(),
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
                            .padding(vertical = dimens.space16, horizontal = dimens.space16),
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
                    TimeMeridiemToggle(
                        isPm = isPm,
                        onSelected = { isPm = it },
                    )
                }
            }

            FutureDateNotice(visible = isFuture)
        }
    }
}

internal class SpinnerStep(
    val contentDescription: String,
    val onClick: () -> Unit,
)

@Composable
internal fun TimeSpinner(
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

/**
 * Compact AM/PM stack, matching the design's two small buttons beside the hour/minute spinner
 * inside the same bordered time box — not a full-width control, so it stays this small
 * intentionally (same compact-tappable-surface precedent as `FilterChip`).
 */
@Composable
internal fun TimeMeridiemToggle(
    isPm: Boolean,
    onSelected: (Boolean) -> Unit,
) {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
        TimeMeridiemButton(
            label = stringResource(R.string.date_time_am),
            selected = !isPm,
            onClick = { onSelected(false) },
        )
        TimeMeridiemButton(
            label = stringResource(R.string.date_time_pm),
            selected = isPm,
            onClick = { onSelected(true) },
        )
    }
}

@Composable
private fun TimeMeridiemButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val shape = ProExpenseTheme.shapes.buttonSm
    val background = if (selected) colors.primary else Color.Transparent
    val contentColor = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant
    val borderColor = if (selected) colors.primary else colors.lineStrong
    val textStyle = if (selected) typography.chipLabelSelected else typography.chipLabel

    Text(
        text = label,
        style = textStyle,
        color = contentColor,
        modifier =
            Modifier
                .proPressScale(interactionSource)
                .clip(shape)
                .background(background)
                .border(1.dp, borderColor, shape)
                .proSelectableClickable(selected = selected, onClick = onClick, interactionSource = interactionSource)
                .padding(horizontal = dimens.space8, vertical = dimens.space2),
    )
}

@Composable
internal fun SectionEyebrow(text: String) {
    Text(
        text = text.uppercase(),
        style = ProExpenseTheme.typography.eyebrow,
        color = ProExpenseTheme.colors.onSurfaceMuted,
    )
}

@Composable
internal fun FutureDateNotice(visible: Boolean) {
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
private fun DateTimePickerScreenPreview() {
    ProExpenseTheme {
        DateTimePickerScreen(
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
private fun DateTimePickerScreenTodayPreview() {
    ProExpenseTheme {
        DateTimePickerScreen(
            visible = true,
            initialEpochMillis = addDays(System.currentTimeMillis(), 2),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
