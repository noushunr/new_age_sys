package com.bizify.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bizify.data.model.AodpList

/**
 * Created by Noushad N on 05-06-2022.
 */
@Dao
interface AodpListDao {
    @Query("SELECT * FROM aodplist")
    fun getAllList(): LiveData<List<AodpList?>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: AodpList?)
}