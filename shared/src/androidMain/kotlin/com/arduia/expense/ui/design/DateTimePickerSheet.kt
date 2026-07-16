package com.arduia.expense.ui.design

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerSheet(
    visible: Boolean,
    initialEpochMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val dimens = ProExpenseTheme.dimensions

    val calendar = Calendar.getInstance().apply { timeInMillis = initialEpochMillis }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis)

    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val timePickerState =
        rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = is24Hour,
        )

    ProBottomSheetHost(
        visible = visible,
        title = "Select Date & Time",
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
                        .padding(top = dimens.space8),
                horizontalArrangement = Arrangement.Center,
            ) {
                MaterialTheme(typography = proDatePickerTypography()) {
                    TimePicker(
                        state = timePickerState,
                        colors = proTimePickerColors(),
                        layoutType = TimePickerLayoutType.Vertical,
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                ProButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Md,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )

                ProButton(
                    text = "Confirm",
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis ?: initialEpochMillis
                        val result =
                            Calendar
                                .getInstance()
                                .apply {
                                    timeInMillis = selectedDate
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
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
        // Fixed instant so the screenshot baseline stays deterministic.
        DateTimePickerSheet(
            visible = true,
            initialEpochMillis = 1_716_600_000_000L,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
