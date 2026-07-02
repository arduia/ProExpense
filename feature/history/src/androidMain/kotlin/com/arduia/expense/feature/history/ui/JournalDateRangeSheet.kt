package com.arduia.expense.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

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
    if (!visible) return

    val dimens = ProExpenseTheme.dimensions
    val rangeState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartEpochMillis,
        initialSelectedEndDateMillis = initialEndEpochMillis,
    )

    ProBottomSheetHost(
        visible = visible,
        title = stringResource(R.string.journal_date_range_title),
        onClose = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            DateRangePicker(
                state = rangeState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                title = null,
                headline = null,
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
