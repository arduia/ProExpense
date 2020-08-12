package com.arduia.expense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.lang.Exception

@Database(
    entities = [ExpenseEnt::class, BackupEnt::class],
    version = 4 )
abstract class  ProExpenseDatabase : RoomDatabase(){

    abstract val expenseDao: ExpenseDao

    abstract val backupDao: BackupDao

}
