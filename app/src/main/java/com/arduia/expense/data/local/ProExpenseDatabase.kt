package com.arduia.expense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.lang.Exception

@Database(
    entities = [ExpenseEnt::class, BackupEnt::class],
    version = 6, exportSchema = true
)
@TypeConverters(AmountTypeConverter::class)
abstract class ProExpenseDatabase : RoomDatabase() {

    abstract val expenseDao: ExpenseDao

    abstract val backupDao: BackupDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `backup` (" +
                            "`backup_id` INTEGER NOT NULL," +
                            "`name` TEXT NOT NULL ," +
                            "`path` TEXT NOT NULL ," +
                            "`created_date` INTEGER NOT NULL ," +
                            "`item_total` INTEGER NOT NULL ," +
                            "`worker_id` TEXT NOT NULL , " +
                            "`is_completed` INTEGER NOT NULL ," +
                            "PRIMARY KEY (`backup_id`) )"
                )
            }
        }
        val MIGRATION_4_6 = object : Migration(4, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE expense RENAME TO  tmp")
                database.execSQL("CREATE TABLE `expense` (`expense_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `amount` INTEGER NOT NULL, `category` INTEGER NOT NULL, `note` TEXT, `created_date` INTEGER NOT NULL, `modified_date` INTEGER NOT NULL)")
                database.execSQL(
                    "INSERT INTO `expense`(`expense_id`, `name`, `amount`, `category`, `note`, `created_date`, `modified_date` ) " +
                            "SELECT `expense_id`, `name`, `amount`, `category`, `note`, `created_date`, `modified_date` " +
                            "FROM tmp; "
                )
                database.execSQL("UPDATE `expense` SET `amount`=`amount`*100;") // Decimal Amount Support. Store Expense with 100 multiplication
                database.execSQL("DROP TABLE tmp;")
            }
        }
    }
}
