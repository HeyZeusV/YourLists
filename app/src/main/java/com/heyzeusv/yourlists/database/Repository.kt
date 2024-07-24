package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.list.ListFilter
import com.heyzeusv.yourlists.overview.OverviewFilter
import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     *  ItemList Queries
     */
    suspend fun insertItemList(vararg itemLists: ItemList)

    suspend fun updateItemList(vararg itemLists: ItemList)

    suspend fun deleteItemList(vararg itemLists: ItemList)

    fun getItemListWithId(id: Long): Flow<ItemList>

    fun getAllItemLists(): Flow<List<ItemListWithItems>>

    fun getSortedItemListsWithItems(filter: OverviewFilter): Flow<List<ItemListWithItems>>

    fun getAllItemListsWithoutId(id: Long): Flow<List<ItemListWithItems>>

    fun getItemListWithItemsWithId(id: Long): Flow<ItemListWithItems?>

    fun getMaxItemListId(): Flow<Long?>

    /**
     *  Item Queries
     */
    suspend fun insertItems(vararg items: Item)

    suspend fun updateItems(vararg items: Item)

    suspend fun deleteItems(vararg items: Item)

    fun getSortedItemsWithParentId(id: Long, filter: ListFilter): Flow<List<Item>>

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
    suspend fun insertCategories(vararg categories: Category)

    fun getAllCategories(): Flow<List<Category>>
}