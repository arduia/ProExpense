package com.arduia.expense.data

interface SecurityStateReader {
    suspend fun hasPinConfigured(): Boolean
}
