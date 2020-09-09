package com.arduia.expense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.lang.Exception

@Database(
    entities = [ExpenseEnt::class, BackupEnt::class],
    version = 4 )
abstract class  ProExpenseDatabase : RoomDatabase(){

    abstract val expenseDao: ExpenseDao

    abstract val backupDao: BackupDao

    companion object{
        val MIGRATION_3_4 =  object:  Migration(3, 4){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `backup` (" +
                    "`backup_id` INTEGER NOT NULL," +
                    "`name` TEXT NOT NULL ," +
                    "`path` TEXT NOT NULL ," +
                    "`created_date` INTEGER NOT NULL ," +
                    "`item_total` INTEGER NOT NULL ," +
                    "`worker_id` TEXT NOT NULL , " +
                    "`is_completed` INTEGER NOT NULL ," +
                    "PRIMARY KEY (`backup_id`) )")
            }
        }
    }
}
