package com.arduia.myacc.data.local

import androidx.paging.DataSource
import androidx.room.*
import com.arduia.myacc.data.local.OweLog

@Dao
interface OweLogDao {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertOweLog(log: OweLog)

    @Query( "SELECT * from owe_log" )
    fun getAllOweLog(): DataSource.Factory<Int, OweLog>

    @Update
    suspend fun updateOweLog(log: OweLog)

    @Delete
    suspend fun deleteOweLog(log: OweLog)

}
