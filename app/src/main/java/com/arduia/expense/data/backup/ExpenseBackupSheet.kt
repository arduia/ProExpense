package com.arduia.expense.data.backup

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.expense.data.local.ExpenseEnt
import java.util.*

class ExpenseBackupSheet( source: BackupSource<ExpenseEnt>): BackupSheet<ExpenseEnt>(source){

    override val sheetName = "expense"

    override fun getFieldNames() = listOf("Name", "Amount", "Category", "Note", "Date")

    override fun create(row: Map<String, String>): ExpenseEnt {

        val name = row["Name"]
        val amount = row["Amount"]?.toLong() ?: 0
        val category = row["Category"]?.toInt() ?: 0
        val note = row["Note"]
        val date = row["Date"]?.toLong() ?: 0

        return ExpenseEnt(0, name, amount, category, note, date, Date().time)
    }

    override fun map(item: ExpenseEnt): Map<String, String> {
       return mutableMapOf<String, String>().apply {
           put("Name", item.name?:"")
           put("Amount", item.amount.toString())
           put("Category", item.category.toString())
           put("Note", item.note?:"")
           put("Date", item.createdDate.toString())
       }
    }
}
