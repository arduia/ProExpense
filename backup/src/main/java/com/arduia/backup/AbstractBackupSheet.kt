package com.arduia.backup

import com.arduia.backup.task.BackupResult

internal interface AbstractBackupSheet<I, O, R> {

    suspend fun import(input: I): BackupResult<R>

    suspend fun export(out: O, index: Int): BackupResult<R>

    suspend fun getItemCount(input: I): Int
}