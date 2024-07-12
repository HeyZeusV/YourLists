package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.heyzeusv.yourlists.database.models.DefaultItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DefaultItemDao {

    @Query("SELECT * " +
            "FROM DefaultItem")
    fun getAllDefaultItems(): Flow<List<DefaultItem>>

    @Query("SELECT * " +
            "FROM DefaultItem " +
            "JOIN DefaultItemFts ON DefaultItem.name = DefaultItemFts.name " +
            "WHERE DefaultItemFts MATCH :query")
    fun searchDefaultItems(query: String): Flow<List<DefaultItem>>
}