package com.arduia.expense.ui.design

import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * M3 `DatePicker`/`DateRangePicker` colors default to Material's baseline purple tonal
 * surfaces (not our overridden `ColorScheme`, which only maps the roles DatePicker doesn't
 * read) — this maps every role the picker actually paints to `ProColors` so it reads as part
 * of the app, not stock Material.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun proDatePickerColors(): DatePickerColors {
    val colors = ProExpenseTheme.colors
    return DatePickerDefaults.colors(
        containerColor = colors.surface,
        titleContentColor = colors.onSurfaceMuted,
        headlineContentColor = colors.onSurface,
        weekdayContentColor = colors.onSurfaceMuted,
        subheadContentColor = colors.onSurface,
        navigationContentColor = colors.onSurface,
        yearContentColor = colors.onSurface,
        currentYearContentColor = colors.primary,
        selectedYearContentColor = colors.onPrimary,
        selectedYearContainerColor = colors.primary,
        dayContentColor = colors.onSurface,
        disabledDayContentColor = colors.muted2,
        selectedDayContentColor = colors.onPrimary,
        disabledSelectedDayContentColor = colors.onPrimary,
        selectedDayContainerColor = colors.primary,
        disabledSelectedDayContainerColor = colors.muted2,
        todayContentColor = colors.primary,
        todayDateBorderColor = colors.primary,
        dayInSelectionRangeContainerColor = colors.primaryTint,
        dayInSelectionRangeContentColor = colors.onSurface,
        dividerColor = colors.line,
    )
}

/**
 * Swaps the M3 default (Roboto) font families for the app's Manrope/Inter set while keeping
 * Material's own sizes — the picker's day cells are laid out to those sizes, so replacing them
 * risks overflow, but the family alone is what reads as "not our app".
 */
@Composable
fun proDatePickerTypography(): Typography {
    val sans = ProExpenseTheme.typography.sansFamily
    val display = ProExpenseTheme.typography.amountFamily
    val base = MaterialTheme.typography
    return base.copy(
        headlineLarge = base.headlineLarge.copy(fontFamily = display),
        titleLarge = base.titleLarge.copy(fontFamily = display),
        titleMedium = base.titleMedium.copy(fontFamily = sans),
        titleSmall = base.titleSmall.copy(fontFamily = sans),
        bodyLarge = base.bodyLarge.copy(fontFamily = sans),
        bodyMedium = base.bodyMedium.copy(fontFamily = sans),
        labelLarge = base.labelLarge.copy(fontFamily = sans),
        labelMedium = base.labelMedium.copy(fontFamily = sans),
        labelSmall = base.labelSmall.copy(fontFamily = sans),
    )
}
