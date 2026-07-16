package com.arduia.expense.ui.design

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * M3 `TimePicker` colors default to Material's baseline purple tonal surfaces (not our
 * overridden `ColorScheme`, which only maps the roles TimePicker doesn't read) — this maps
 * every role the picker actually paints to `ProColors` so it reads as part of the app, not
 * stock Material.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun proTimePickerColors(): TimePickerColors {
    val colors = ProExpenseTheme.colors
    return TimePickerDefaults.colors(
        clockDialColor = colors.paperAlt,
        clockDialSelectedContentColor = colors.onPrimary,
        clockDialUnselectedContentColor = colors.onSurface,
        selectorColor = colors.primary,
        containerColor = colors.surface,
        periodSelectorBorderColor = colors.line,
        periodSelectorSelectedContainerColor = colors.primary,
        periodSelectorUnselectedContainerColor = colors.surface,
        periodSelectorSelectedContentColor = colors.onPrimary,
        periodSelectorUnselectedContentColor = colors.onSurface,
        timeSelectorSelectedContainerColor = colors.primaryTint,
        timeSelectorUnselectedContainerColor = colors.paperAlt,
        timeSelectorSelectedContentColor = colors.primary,
        timeSelectorUnselectedContentColor = colors.onSurface,
    )
}
