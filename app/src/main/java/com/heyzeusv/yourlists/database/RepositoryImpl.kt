package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.DefaultItemDao
import com.heyzeusv.yourlists.database.dao.ItemListDao
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val itemListDao: ItemListDao,
    private val defaultItemDao: DefaultItemDao,
    private val categoryDao: CategoryDao,
) : Repository {
    /**
     *  ItemList Queries
     */
    override suspend fun insertItemList(vararg itemLists: ItemList): Long =
        withContext(Dispatchers.IO) { itemListDao.insert(*itemLists).first() }

    override suspend fun updateItemList(vararg itemLists: ItemList) =
        withContext(Dispatchers.IO) { itemListDao.update(*itemLists) }

    override suspend fun deleteItemList(vararg itemLists: ItemList) =
        withContext(Dispatchers.IO) { itemListDao.delete(*itemLists) }

    override fun getAllItemLists(): Flow<List<ItemListWithItems>> =
        itemListDao.getAllItemListsWithItems()

    override suspend fun getItemListWithId(id: Long): ItemListWithItems? =
        withContext(Dispatchers.IO) { itemListDao.getItemListWithId(id) }

    /**
     *  DefaultItem Queries
     */
    override fun getAllDefaultItems(): Flow<List<DefaultItem>> =
        defaultItemDao.getAllDefaultItems()

    override fun searchDefaultItems(query: String): Flow<List<DefaultItem>> =
        defaultItemDao.searchDefaultItems(query)

    /**
     *  Category Queries
     */
    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategories()
    }
}