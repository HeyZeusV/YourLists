package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heyzeusv.yourlists.database.models.ItemListWithItems

@Dao
interface ItemListDao {

    @Transaction
    @Query("SELECT * FROM ItemList")
    suspend fun getAllItemListsWithItems(): List<ItemListWithItems>
}