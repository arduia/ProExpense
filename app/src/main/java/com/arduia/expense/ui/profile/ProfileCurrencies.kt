package com.arduia.expense.ui.profile

data class ProfileCurrencyOption(
    val code: String,
    val symbol: String,
    val name: String,
)

val profileCurrencyOptions = listOf(
    ProfileCurrencyOption("USD", "$", "US Dollar"),
    ProfileCurrencyOption("EUR", "€", "Euro"),
    ProfileCurrencyOption("GBP", "£", "British Pound"),
    ProfileCurrencyOption("JPY", "¥", "Japanese Yen"),
    ProfileCurrencyOption("INR", "₹", "Indian Rupee"),
    ProfileCurrencyOption("AED", "د.إ", "UAE Dirham"),
)
