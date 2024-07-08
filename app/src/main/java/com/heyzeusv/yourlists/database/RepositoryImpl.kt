package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.dao.ItemListDao
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val itemListDao: ItemListDao
) : Repository {
    override suspend fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>> =
        itemListDao.getAllItemListsWithItems()
}