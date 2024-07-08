package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     *  ItemList Queries
     */
    suspend fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>>
}