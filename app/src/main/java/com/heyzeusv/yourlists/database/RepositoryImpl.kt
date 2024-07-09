package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.ItemListDao
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val itemListDao: ItemListDao,
    private val categoryDao: CategoryDao
) : Repository {
    /**
     *  ItemList Queries
     */
    override fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>> =
        itemListDao.getAllItemListsWithItems()

    override suspend fun getItemListWithId(id: Long): ItemListWithItems? =
        withContext(Dispatchers.IO) { itemListDao.getItemListWithId(id) }

    /**
     *  Category Queries
     */
    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategories()
    }
}