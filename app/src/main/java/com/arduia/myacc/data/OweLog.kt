package com.arduia.myacc.data

import androidx.room.*

@Entity( tableName = "owe_log" )
data class OweLog(

    @PrimaryKey( autoGenerate = true )
    @ColumnInfo( name = "owe_log_id" )
    val id: Int = 0,

    @Embedded
    val owe_people: OwePeople?,

    @ColumnInfo( name = "taken")
    val direction: String,

    @ColumnInfo( name = "start_date")
    val start_date: Long,

    @ColumnInfo( name = "last_date" )
    val last_date: Long,

    @ColumnInfo( name = "value" )
    val value: Long,

    @ColumnInfo( name = "note" )
    val note: String

)
