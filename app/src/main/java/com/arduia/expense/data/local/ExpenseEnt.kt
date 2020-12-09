package com.arduia.expense.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ExpenseEnt.TABLE_NAME)
data class ExpenseEnt(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expenseId: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "amount")
    val amount: Float,

    @ColumnInfo(name = "category")
    val category: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_date")
    val createdDate: Long,

    @ColumnInfo(name = "modified_date")
    val modifiedDate: Long
){
    companion object{
        const val TABLE_NAME = "expense"
    }
}
