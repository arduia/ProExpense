package com.arduia.backup

interface BackupSource <T> {

    suspend fun writeSingleItem(item: T)

    suspend fun writeAllItem(items: List<T>)

    suspend fun readAllItem(): List<T>

}
