package com.arduia.expense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.lang.Exception

@Database(
    entities = [ExpenseEnt::class],
    version = 3 )
abstract class AccountingDatabase : RoomDatabase(){

    abstract val expenseDao: ExpenseDao

}
