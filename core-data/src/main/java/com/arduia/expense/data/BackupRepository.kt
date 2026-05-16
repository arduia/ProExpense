package com.arduia.expense.data

import android.net.Uri
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.FlowResult
import kotlinx.coroutines.flow.Flow

interface BackupRepository {

    suspend fun insertBackup(item: BackupEnt)

    suspend fun updateBackup(item: BackupEnt)

    suspend fun deleteBackup(item: BackupEnt)

    suspend fun deleteBackupByID(id: Int)

    fun getBackupAll(): FlowResult<List<BackupEnt>>

    fun getBackupByID(id: Int): FlowResult<BackupEnt>

    fun getBackupByWorkerID(id: String): FlowResult<BackupEnt>

    fun getItemCount(uri: Uri): FlowResult<Int>

}
