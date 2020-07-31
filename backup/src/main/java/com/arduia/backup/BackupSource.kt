package com.arduia.backup

interface BackupSource <T> {

    suspend fun write(item: T)

    suspend fun writeAll(items: List<T>)

    suspend fun readAll(): List<T>

}
