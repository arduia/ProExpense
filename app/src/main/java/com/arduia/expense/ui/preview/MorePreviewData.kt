package com.arduia.expense.ui.preview

import com.arduia.expense.ui.design.ProIconGlyph

data class MoreProfileUi(
    val initial: String,
    val name: String,
    val subtitle: String,
)

data class MoreFeatureRowUi(
    val id: String,
    val icon: ProIconGlyph,
    val label: String,
)

enum class MoreSettingKind { Nav, Toggle, Value }

data class MoreSettingRowUi(
    val id: String,
    val icon: ProIconGlyph,
    val label: String,
    val value: String? = null,
    val kind: MoreSettingKind = MoreSettingKind.Nav,
    val toggleOn: Boolean = false,
    val enabled: Boolean = true,
)

data class MoreHubUiState(
    val profile: MoreProfileUi,
    val features: List<MoreFeatureRowUi>,
    val settings: List<MoreSettingRowUi>,
)

data class MoreCurrencyItemUi(
    val code: String,
    val name: String,
    val symbol: String,
)

data class MoreExportFileUi(
    val fileName: String,
    val subtitle: String,
)

data class MoreClearOptionUi(
    val id: String,
    val label: String,
    val subtitle: String,
    val destructive: Boolean = false,
)

val previewMoreHub = MoreHubUiState(
    profile = MoreProfileUi(
        initial = "M",
        name = "Maya",
        subtitle = "All data local · no account",
    ),
    features = listOf(
        MoreFeatureRowUi("reports", ProIconGlyph.FeatReports, "Reports"),
        MoreFeatureRowUi("debt", ProIconGlyph.FeatDebt, "Debt Tracker"),
        MoreFeatureRowUi("shared", ProIconGlyph.FeatSplit, "Shared Costs"),
        MoreFeatureRowUi("categories", ProIconGlyph.CatDefault, "Category List"),
    ),
    settings = listOf(
        MoreSettingRowUi("currency", ProIconGlyph.Budget, "Currency", value = "USD", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("budget", ProIconGlyph.Note, "Monthly budget", value = "Off", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("pin", ProIconGlyph.Eye, "PIN authentication", value = "Off", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("biometric", ProIconGlyph.Fingerprint, "Biometric unlock", kind = MoreSettingKind.Toggle, toggleOn = false, enabled = false),
        MoreSettingRowUi("category", ProIconGlyph.CatDefault, "Default category", value = "Food", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("language", ProIconGlyph.Note, "Language", value = "English", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("theme", ProIconGlyph.Sparkle, "Theme", value = "System", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("export", ProIconGlyph.Note, "Data export", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("clear", ProIconGlyph.Close, "Clear data", kind = MoreSettingKind.Nav),
        MoreSettingRowUi("version", ProIconGlyph.Bell, "App version", value = "2.0.0", kind = MoreSettingKind.Value),
    ),
)

val previewMoreCurrencies = listOf(
    MoreCurrencyItemUi("USD", "US Dollar", "$"),
    MoreCurrencyItemUi("EUR", "Euro", "€"),
    MoreCurrencyItemUi("GBP", "British Pound", "£"),
    MoreCurrencyItemUi("JPY", "Japanese Yen", "¥"),
    MoreCurrencyItemUi("INR", "Indian Rupee", "₹"),
    MoreCurrencyItemUi("AED", "UAE Dirham", "د.إ"),
)

val previewMoreExportFiles = listOf(
    MoreExportFileUi("expenses.csv", "All logged expenses + @ tags"),
    MoreExportFileUi("events.csv", "Event budgets & status"),
    MoreExportFileUi("debts.csv", "Lent / owed records"),
    MoreExportFileUi("shared_costs.csv", "Saved bill splits"),
)

val previewMoreClearOptions = listOf(
    MoreClearOptionUi("expenses", "Expenses", "All logged entries"),
    MoreClearOptionUi("events", "Events", "Budgets & linked tags"),
    MoreClearOptionUi("debts", "Debts", "Lent / owed records"),
    MoreClearOptionUi("shared", "Shared costs", "Saved splits"),
    MoreClearOptionUi("everything", "Everything", "Reset the app fully", destructive = true),
)
