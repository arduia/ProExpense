package com.arduia.expense.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(item: BackupEnt)

    @Delete
    suspend fun deleteBackup(item: BackupEnt)

    @Query("DELETE FROM backup WHERE backup_id =:id")
    fun deleteBackupByID(id: Int)

    @Update
    suspend fun updateBackup(item: BackupEnt)

    @Query("SELECT * FROM backup WHERE backup_id =:id ")
    fun getBackupByID(id: Int): Flow<BackupEnt>

    @Query("SELECT * FROM backup ORDER BY created_date DESC")
    fun getBackupAll(): Flow<List<BackupEnt>>


}