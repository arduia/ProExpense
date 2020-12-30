package com.arduia.backup.task

interface BackupTask<R> {
    val id: Int
    val name: String?
    val result: BackupResult<R>
}