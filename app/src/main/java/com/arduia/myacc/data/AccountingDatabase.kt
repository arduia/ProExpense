package com.arduia.myacc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Transaction::class, OwePeople::class, OweLog::class],
    version = 3 )
abstract class AccountingDatabase : RoomDatabase(){

    abstract val transactionDao: TransactionDao
    abstract val owePeopleDao: OwePeopleDao
    abstract val oweLogDao: OweLogDao

    companion object{

        @Volatile
        private var INSTANCE: AccountingDatabase? = null

        @Synchronized
        fun getInstance(context: Context) =
            Room.databaseBuilder(context.applicationContext,
            AccountingDatabase::class.java,
            "accounting.db").build()
//
//            : AccountingDatabase{
//            INSTANCE?.let {
//                val db = Room.databaseBuilder(context.applicationContext,
//                    AccountingDatabase::class.java,
//                    "accounting.db").build()
//                INSTANCE = db
//            }
//            return INSTANCE!!
//        }

    }

}
