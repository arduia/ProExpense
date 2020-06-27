package com.arduia.myacc.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.arduia.myacc.data.local.OwePeople

@Dao
interface OwePeopleDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertOwePeople( people: OwePeople)

    @Query( "SELECT * FROM owe_people")
    fun getAllOwePeople(): PagingSource<Int, OwePeople>

    @Delete
    suspend fun deleteOwePeople( people: OwePeople)

    @Update
    suspend fun updateOwePeople( people: OwePeople)

}
