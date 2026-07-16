package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Single-date full-screen picker (e.g. a debt due date) matching the Claude Design "Date & Time
 * Picker — Dev Handoff" spec's `DatePickerSheet`. When [allowClear] is true, a secondary Clear
 * button sits beside Apply — the field this feeds is optional and previously had no way back to
 * "unset" once a date was picked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(
    visible: Boolean,
    title: String,
    initialEpochMillis: Long?,
    allowClear: Boolean,
    onApply: (Long) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val dimens = ProExpenseTheme.dimensions

    // See DateTimePickerScreen for why this keys on the initial value, not `visible`.
    val datePickerState =
        key(initialEpochMillis) {
            rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis?.let(::startOfDay))
        }
    val selected = datePickerState.selectedDateMillis

    PickerScreenShell(
        visible = visible,
        title = title,
        onClose = onDismiss,
        footer = {
            val useLabel =
                selected?.let { stringResource(R.string.date_picker_use, PlatformDateFormatter.shortDateLabel(it)) }
                    ?: stringResource(R.string.date_picker_select)
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
                if (allowClear) {
                    ProButton(
                        text = stringResource(R.string.date_picker_clear),
                        onClick = {
                            onClear()
                            onDismiss()
                        },
                        variant = ProButtonVariant.Secondary,
                        size = ProButtonSize.Md,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                ProButton(
                    text = useLabel,
                    onClick = {
                        selected?.let {
                            onApply(it)
                            onDismiss()
                        }
                    },
                    enabled = selected != null,
                    size = ProButtonSize.Md,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    ) {
        MaterialTheme(typography = proDatePickerTypography()) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = proDatePickerColors(),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(
    name = "Date picker — empty, clearable",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DatePickerScreenEmptyPreview() {
    ProExpenseTheme {
        DatePickerScreen(
            visible = true,
            title = "Due date",
            initialEpochMillis = null,
            allowClear = true,
            onApply = {},
            onClear = {},
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Date picker — selected",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DatePickerScreenSelectedPreview() {
    ProExpenseTheme {
        DatePickerScreen(
            visible = true,
            title = "Due date",
            initialEpochMillis = 1_716_600_000_000L,
            allowClear = true,
            onApply = {},
            onClear = {},
            onDismiss = {},
        )
    }
}
