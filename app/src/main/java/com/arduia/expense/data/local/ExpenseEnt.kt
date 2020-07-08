package com.arduia.expense.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseEnt(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expense_id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "category")
    val category: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_date")
    val created_date: Long,

    @ColumnInfo(name = "modified_date")
    val modified_date: Long

)
