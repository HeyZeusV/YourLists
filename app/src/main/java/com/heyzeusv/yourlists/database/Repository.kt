package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     *  ItemList Queries
     */
    suspend fun insertItemList(vararg itemLists: ItemList): Long

    fun getAllItemLists(): Flow<List<ItemListWithItems>>

    suspend fun getItemListWithId(id: Long): ItemListWithItems?

    /**
     *  Category Queries
     */
    suspend fun getAllCategories(): List<Category>
}