package com.arduia.myacc.data.local

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

    companion object{

        @Volatile
        private var INSTANCE: AccountingDatabase? = null

        fun getInstance(context: Context): AccountingDatabase {
            if(INSTANCE == null){
                synchronized(this){
                    if(INSTANCE == null){
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AccountingDatabase::class.java,
                            "accounting.db"
                            ).fallbackToDestructiveMigration()
                            .build()
                    }
                }

            }

            return INSTANCE
                ?: throw Exception("Database instance is null")
        }
    }

}
