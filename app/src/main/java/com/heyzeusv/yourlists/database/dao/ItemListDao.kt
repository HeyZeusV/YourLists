package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemListDao {

    @Transaction
    @Query("SELECT * " +
            "FROM ItemList")
    fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>>

    @Query("SELECT * " +
            "FROM ItemList " +
            "WHERE itemListId=(:id)")
    suspend fun getItemListWithId(id: Long): ItemListWithItems?
}