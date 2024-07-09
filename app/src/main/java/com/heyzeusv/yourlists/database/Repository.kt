package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     *  ItemList Queries
     */
    fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>>

    suspend fun getItemListWithId(id: Long): ItemListWithItems?

    /**
     *  Category Queries
     */
    suspend fun getAllCategories(): List<Category>
}