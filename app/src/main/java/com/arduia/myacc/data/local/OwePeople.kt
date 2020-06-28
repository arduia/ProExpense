package com.arduia.myacc.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owe_people")
data class OwePeople(

    @PrimaryKey( autoGenerate = true )
    @ColumnInfo( name = "owe_people_id" )
    val owe_people_id: Int,

    @ColumnInfo( name = "name")
    val name: String,

    @ColumnInfo( name = "phone_number" )
    val phone_name: String

)
