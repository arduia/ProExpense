package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    val calendar = Calendar.getInstance().apply { timeInMillis = initialEpochMillis }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis)

    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    ProBottomSheetHost(
        visible = visible,
        title = "Select Date & Time",
        onClose = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            val typography = ProExpenseTheme.typography
            val timeFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.line,
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.muted,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
                cursorColor = colors.primary,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
            )

            MaterialTheme(typography = proDatePickerTypography()) {
                DatePicker(
                    state = datePickerState,
                    title = null,
                    headline = null,
                    colors = proDatePickerColors(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space8),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = selectedHour.toString().padStart(2, '0'),
                    onValueChange = { value ->
                        selectedHour = value.toIntOrNull()?.coerceIn(0, 23) ?: selectedHour
                    },
                    label = { Text("Hour", style = typography.caption) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    visualTransformation = TimeFieldTransformation(),
                    textStyle = typography.body,
                    shape = ProExpenseTheme.shapes.searchField,
                    colors = timeFieldColors,
                )

                Text(":", style = typography.body, color = colors.onSurface, modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = selectedMinute.toString().padStart(2, '0'),
                    onValueChange = { value ->
                        selectedMinute = value.toIntOrNull()?.coerceIn(0, 59) ?: selectedMinute
                    },
                    label = { Text("Min", style = typography.caption) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    visualTransformation = TimeFieldTransformation(),
                    textStyle = typography.body,
                    shape = ProExpenseTheme.shapes.searchField,
                    colors = timeFieldColors,
                )
            }

            Row(
                modifier = Modifier
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
                        val result = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
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

private class TimeFieldTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
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
