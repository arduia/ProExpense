package com.arduia.myacc.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OwePeopleDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertOwePeople( people: OwePeople )

    @Query( "SELECT * FROM owe_people")
    suspend fun getAllOwePeople(): List<OwePeople>

    @Delete
    suspend fun deleteOwePeople( people: OwePeople )

    @Update
    suspend fun updateOwePeople( people: OwePeople )

}
