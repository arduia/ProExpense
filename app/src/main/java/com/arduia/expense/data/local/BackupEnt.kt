package com.arduia.expense.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup")
data class BackupEnt(

    @ColumnInfo(name = "backup_id")
    @PrimaryKey(autoGenerate = true)
    val backupID: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "created_date")
    val created_date: Long,

    @ColumnInfo(name = "total_items")
    val total_items: Int,

    @ColumnInfo(name = "progress_items")
    val progress_items: Int,

    //Import or Export
    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "is_exported")
    val isExported: Boolean

)