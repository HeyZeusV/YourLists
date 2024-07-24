package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.DefaultItemDao
import com.heyzeusv.yourlists.database.dao.ItemDao
import com.heyzeusv.yourlists.database.dao.ItemListDao
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.list.ListFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val itemListDao: ItemListDao,
    private val itemDao: ItemDao,
    private val defaultItemDao: DefaultItemDao,
    private val categoryDao: CategoryDao,
) : Repository {
    /**
     *  ItemList Queries
     */
    override suspend fun insertItemList(vararg itemLists: ItemList) =
        withContext(Dispatchers.IO) { itemListDao.insert(*itemLists) }

    override suspend fun updateItemList(vararg itemLists: ItemList) =
        withContext(Dispatchers.IO) { itemListDao.update(*itemLists) }

    override suspend fun deleteItemList(vararg itemLists: ItemList) =
        withContext(Dispatchers.IO) { itemListDao.delete(*itemLists) }

    override fun getItemListWithId(id: Long): Flow<ItemList> = itemListDao.getItemListWithId(id)

    override fun getAllItemLists(): Flow<List<ItemListWithItems>> =
        itemListDao.getAllItemListsWithItems()

    override fun getAllItemListsWithoutId(id: Long): Flow<List<ItemListWithItems>> =
        itemListDao.getAllItemListsWithoutId(id)

    override fun getItemListWithItemsWithId(id: Long): Flow<ItemListWithItems?> =
        itemListDao.getItemListWithItemsWithId(id)

    override fun getMaxItemListId(): Flow<Long?> = itemListDao.getMaxItemListId()

    /**
     *  Item Queries
     */
    override suspend fun insertItems(vararg items: Item) =
        withContext(Dispatchers.IO) { itemDao.insert(*items) }

    override suspend fun updateItems(vararg items: Item) =
        withContext(Dispatchers.IO) { itemDao.update(*items) }

    override suspend fun deleteItems(vararg items: Item) =
        withContext(Dispatchers.IO) { itemDao.delete(*items) }

    override fun getSortedItemsWithParentId(id: Long, filter: ListFilter): Flow<List<Item>> =
        itemDao.getSortedItemsWithParentId(
            id = id,
            byIsChecked = filter.byIsChecked,
            byIsCheckedOption = filter.byIsCheckedOption.value,
            byCategory = filter.byCategory,
            byCategoryOption = filter.byCategoryOption.value,
            byName = filter.byName,
            byNameOption = filter.byNameOption.value,
        )

    /**
     *  DefaultItem Queries
     */
    override suspend fun upsertDefaultItems(vararg defaultItems: DefaultItem) =
        withContext(Dispatchers.IO) { defaultItemDao.upsert(*defaultItems) }

    override suspend fun deleteDefaultItems(vararg defaultItems: DefaultItem) =
        withContext(Dispatchers.IO) { defaultItemDao.delete(*defaultItems) }

    override fun getAllDefaultItems(): Flow<List<DefaultItem>> =
        defaultItemDao.getAllDefaultItems()

    override fun searchDefaultItems(query: String): Flow<List<DefaultItem>> =
        defaultItemDao.searchDefaultItems(query)

    /**
     *  Category Queries
     */
    override suspend fun insertCategories(vararg categories: Category) =
        withContext(Dispatchers.IO) { categoryDao.insert(*categories) }

    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
}