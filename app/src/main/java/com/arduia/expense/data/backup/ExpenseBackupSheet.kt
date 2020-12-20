package com.arduia.expense.data.backup

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.getActualAsFloat
import java.math.BigDecimal
import java.util.*

class ExpenseBackupSheet(source: BackupSource<ExpenseEnt>) : BackupSheet<ExpenseEnt>(source) {

    override val sheetName = "expense"

    override fun getSheetFieldNames() =
        listOf(FIELD_NAME, FIELD_AMOUNT, FIELD_CATEGORY, FIELD_NOTE, FIELD_DATE)

    override fun mapToEntityFromSheetData(row: Map<String, String>): ExpenseEnt {

        val name = row[FIELD_NAME]
        val amount = BigDecimal(row[FIELD_AMOUNT] ?: "0.0")
        val category = row[FIELD_CATEGORY]?.toInt() ?: 0
        val note = row[FIELD_NOTE]
        val date = row[FIELD_DATE]?.toLong() ?: 0

        return ExpenseEnt(
            0,
            name,
            Amount.createFromActual(amount),
            category,
            note,
            createdDate = Date().time,
            modifiedDate = date
        )
    }

    override fun mapToSheetDataFromEntity(item: ExpenseEnt): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put(FIELD_NAME, item.name ?: "")
            put(FIELD_AMOUNT, item.amount.getActual().toString())
            put(FIELD_CATEGORY, item.category.toString())
            put(FIELD_NOTE, item.note ?: "")
            put(FIELD_DATE, item.createdDate.toString())
        }
    }

    companion object {
        private const val FIELD_NAME = "Name"
        private const val FIELD_AMOUNT = "Amount"
        private const val FIELD_CATEGORY = "Category"
        private const val FIELD_NOTE = "Note"
        private const val FIELD_DATE = "Date"
    }
}
