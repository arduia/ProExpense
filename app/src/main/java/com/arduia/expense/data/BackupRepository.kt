package com.arduia.expense.data

import com.arduia.expense.data.local.BackupEnt
import kotlinx.coroutines.flow.Flow

interface BackupRepository {

    suspend fun insertBackup(item: BackupEnt)

    suspend fun updateBackup(item: BackupEnt)

    suspend fun deleteBackup(item: BackupEnt)

    suspend fun deleteBackupByID(id: Int)

    suspend fun getBackupAll(): Flow<List<BackupEnt>>

    suspend fun getBackupByID(id: Int): Flow<BackupEnt>

    suspend fun getBackupByWorkerID(id: String): Flow<BackupEnt>

    suspend fun getItemCount(filePath: String): Flow<Int>

}
