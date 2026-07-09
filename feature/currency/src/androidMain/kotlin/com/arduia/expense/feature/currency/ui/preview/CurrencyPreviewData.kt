package com.arduia.expense.feature.currency.ui.preview

data class MoreCurrencyItemUi(
    val code: String,
    val name: String,
    val symbol: String,
)

val previewMoreCurrencies =
    listOf(
        MoreCurrencyItemUi("USD", "US Dollar", "$"),
        MoreCurrencyItemUi("EUR", "Euro", "€"),
        MoreCurrencyItemUi("GBP", "British Pound", "£"),
        MoreCurrencyItemUi("JPY", "Japanese Yen", "¥"),
        MoreCurrencyItemUi("INR", "Indian Rupee", "₹"),
        MoreCurrencyItemUi("AED", "UAE Dirham", "د.إ"),
    )
