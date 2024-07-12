package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     *  ItemList Queries
     */
    suspend fun insertItemList(vararg itemLists: ItemList): Long

    suspend fun updateItemList(vararg itemLists: ItemList)

    suspend fun deleteItemList(vararg itemLists: ItemList)

    fun getAllItemLists(): Flow<List<ItemListWithItems>>

    suspend fun getItemListWithId(id: Long): ItemListWithItems?

    /**
     *  DefaultItem Queries
     */
    fun getAllDefaultItems(): Flow<List<DefaultItem>>

    fun searchDefaultItems(query: String): Flow<List<DefaultItem>>

    /**
     *  Category Queries
     */
    suspend fun getAllCategories(): List<Category>
}