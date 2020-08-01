package com.arduia.expense.data

import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.local.BackupDao
import com.arduia.expense.data.local.BackupEnt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BackupRepositoryImpl (private val dao: BackupDao,
                            private val backup: ExcelBackup): BackupRepository{

    override suspend fun insertBackup(item: BackupEnt) {
        dao.insertBackup(item)
    }

    override suspend fun updateBackup(item: BackupEnt) {
        dao.updateBackup(item)
    }

    override suspend fun deleteBackup(item: BackupEnt) {
        dao.deleteBackup(item)
    }

    override suspend fun deleteBackupByID(id: Int) {
        dao.deleteBackupByID(id)
    }

    override suspend fun getBackupAll() = dao.getBackupAll()

    override suspend fun getBackupByID(id: Int) = dao.getBackupByID(id)

    override suspend fun getBackupByWorkerID(id: String): Flow<BackupEnt> {
        return dao.getBackupByWorkerID(id)
    }

    override suspend fun getItemCount(filePath: String) =
        flow { emit(backup.itemCount(filePath)) }
}
