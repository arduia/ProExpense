package com.arduia.expense.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup")
data class BackupEnt(

    @ColumnInfo(name = "backup_id")
    @PrimaryKey(autoGenerate = true)
    val backupId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "path")
    val filePath: String,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    @ColumnInfo(name = "item_total")
    var itemTotal: Int,

    @ColumnInfo(name = "worker_id")
    val workerId: String,

    @ColumnInfo(name = "is_completed")
    var isCompleted : Boolean

)
