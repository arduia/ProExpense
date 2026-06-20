package com.arduia.expense.feature.importexport

expect object HmacSha256 {
    fun sign(key: String, data: String): String

    fun verify(key: String, data: String, signature: String): Boolean
}
