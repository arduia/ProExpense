package com.arduia.myacc.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OweLogDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertOweLog(log: OweLog)

    @Query( "SELECT * from owe_log" )
    suspend fun getAllOweLog(): List<OweLog>

    @Update
    suspend fun updateOweLog(log: OweLog)

    @Delete
    suspend fun deleteOweLog(log: OweLog)

}
