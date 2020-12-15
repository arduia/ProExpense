package com.arduia.expense.data.local

import androidx.room.*
import com.arduia.expense.domain.Amount

@Entity(tableName = ExpenseEnt.TABLE_NAME)
data class ExpenseEnt(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expenseId: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @TypeConverters(AmountTypeConverter::class)
    @ColumnInfo(name = "amount")
    val amount: Amount,

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
