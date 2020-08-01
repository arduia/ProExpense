package com.arduia.expense.data.backup

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.expense.data.local.ExpenseEnt
import java.util.*

class ExpenseBackupSheet( source: BackupSource<ExpenseEnt>): BackupSheet<ExpenseEnt>(source){

    override val sheetName = "expense"

    override fun getFieldNames() = listOf(FIELD_NAME, FIELD_AMOUNT, FIELD_CATEGORY, FIELD_NOTE, FIELD_DATE)

    override fun create(row: Map<String, String>): ExpenseEnt {

        val name = row[FIELD_NAME]
        val amount = row[FIELD_AMOUNT]?.toLong() ?: 0
        val category = row[FIELD_CATEGORY]?.toInt() ?: 0
        val note = row[FIELD_NOTE]
        val date = row[FIELD_DATE]?.toLong() ?: 0

        return ExpenseEnt(0, name, amount, category, note, date, Date().time)
    }

    override fun map(item: ExpenseEnt): Map<String, String> {
       return mutableMapOf<String, String>().apply {
           put(FIELD_NAME, item.name?:"")
           put(FIELD_AMOUNT, item.amount.toString())
           put(FIELD_CATEGORY, item.category.toString())
           put(FIELD_NOTE, item.note?:"")
           put(FIELD_DATE, item.createdDate.toString())
       }
    }

    companion object{
        private const val FIELD_NAME = "Name"
        private const val FIELD_AMOUNT = "Amount"
        private const val FIELD_CATEGORY = "Category"
        private const val FIELD_NOTE = "Note"
        private const val FIELD_DATE = "Date"
    }
}
