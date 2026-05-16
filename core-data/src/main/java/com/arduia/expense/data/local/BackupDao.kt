package com.arduia.expense.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBackup(item: BackupEnt): Long

    @Delete
    fun deleteBackup(item: BackupEnt): Int

    @Update
    fun updateBackup(item: BackupEnt): Int

    @Query("DELETE FROM backup WHERE backup_id =:id")
    fun deleteBackupByID(id: Int)

    @Query("SELECT * FROM backup WHERE backup_id =:id ")
    fun getBackupByID(id: Int): Flow<BackupEnt>

    @Query("SELECT * FROM backup WHERE worker_id =:worker_id")
    fun getBackupByWorkerID(worker_id: String): Flow<BackupEnt>

    @Query("SELECT * FROM backup ORDER BY created_date DESC")
    fun getBackupAll(): Flow<List<BackupEnt>>

}
