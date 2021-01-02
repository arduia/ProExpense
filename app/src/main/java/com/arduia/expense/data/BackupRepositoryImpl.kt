package com.arduia.expense.data

import android.content.Context
import android.net.Uri
import com.arduia.backup.BackupException
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.BackupDao
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.lang.Exception
import javax.inject.Inject


class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dao: BackupDao,
    private val backup: ExcelBackup
) : BackupRepository {

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

    override fun getBackupAll(): FlowResult<List<BackupEnt>> {
        return dao.getBackupAll()
            .map {  SuccessResult(it) }
            .catch { e -> ErrorResult(Exception(e)) }
    }

    override fun getBackupByID(id: Int): FlowResult<BackupEnt> {
        return dao.getBackupByID(id)
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override fun getBackupByWorkerID(id: String): FlowResult<BackupEnt> {
        return dao.getBackupByWorkerID(id)
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override fun getItemCount(uri: Uri): FlowResult<Int> = flow {
        try {
            val contentResolver = appContext.contentResolver
            val inputStream =
                contentResolver.openInputStream(uri) ?: throw Exception("Cannot open Input Stream")
            val itemCount = backup.itemCount(inputStream)

            emit(itemCount)

        } catch (e: BackupException) {
            emit(-1)
            return@flow
        }
    }.map { SuccessResult(it) }
        .catch { e -> ErrorResult(RepositoryException(e)) }
}
