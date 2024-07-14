package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
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
     *  Item Queries
     */
    suspend fun insertItems(vararg items: Item): Long

    suspend fun updateItems(vararg items: Item)

    suspend fun deleteItems(vararg items: Item)

    /**
     *  DefaultItem Queries
     */
    suspend fun upsertDefaultItems(vararg defaultItems: DefaultItem)

    suspend fun deleteDefaultItems(vararg defaultItems: DefaultItem)

    fun getAllDefaultItems(): Flow<List<DefaultItem>>

    fun searchDefaultItems(query: String): Flow<List<DefaultItem>>

    /**
     *  Category Queries
     */
    suspend fun upsertCategories(vararg categories: Category)

    fun getAllCategories(): Flow<List<Category>>
}