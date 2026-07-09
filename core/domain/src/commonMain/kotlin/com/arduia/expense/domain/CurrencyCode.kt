package com.arduia.expense.domain

data class CurrencyCode(
    val code: String,
) {
    init {
        require(code.length == 3 && code.all { it.isUpperCase() }) {
            "Currency code must be a 3-letter ISO code"
        }
    }
}
