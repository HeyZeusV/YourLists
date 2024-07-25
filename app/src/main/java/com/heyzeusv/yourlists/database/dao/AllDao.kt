package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class AllDao {

    @Query("INSERT INTO DefaultItemFts(DefaultItemFts) VALUES ('rebuild')")
    abstract fun rebuildDefaultItemFts()

    @Query("DELETE FROM sqlite_sequence")
    abstract fun deleteAllPrimaryKeys()
}