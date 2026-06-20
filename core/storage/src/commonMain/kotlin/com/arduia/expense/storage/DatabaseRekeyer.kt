package com.arduia.expense.storage

interface DatabaseRekeyer {
    suspend fun rekey(newPassphrase: ByteArray)
}
