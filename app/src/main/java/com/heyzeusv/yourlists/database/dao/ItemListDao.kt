package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.FilterValue.ASC
import com.heyzeusv.yourlists.util.FilterValue.DESC
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemListDao : BaseDao<ItemList> {

    @Query("SELECT * " +
            "FROM ItemList " +
            "WHERE itemListId=(:id)")
    fun getItemListWithId(id: Long): Flow<ItemList>

    @Transaction
    @Query("SELECT * " +
            "FROM ItemList")
    fun getAllItemListsWithItems(): Flow<List<ItemListWithItems>>

    @Transaction
    @Query("SELECT * " +
            "FROM ItemList " +
            "ORDER BY " +
            "CASE WHEN :byName = 1 AND :byNameOption = '$ASC' THEN name END ASC, " +
            "CASE WHEN :byName = 1 AND :byNameOption = '$DESC' THEN name End DESC")
    fun getSortedItemListsWithItems(
        byName: Boolean,
        byNameOption: String
    ): Flow<List<ItemListWithItems>>

    @Transaction
    @Query("SELECT * " +
            "FROM ItemList " +
            "WHERE itemListId IS NOT (:id)")
    fun getAllItemListsWithoutId(id: Long): Flow<List<ItemListWithItems>>

    @Transaction
    @Query("SELECT * " +
            "FROM ItemList " +
            "WHERE itemListId=(:id)")
    fun getItemListWithItemsWithId(id: Long): Flow<ItemListWithItems?>

    @Query("SELECT MAX(itemListId) " +
            "FROM ItemList")
    fun getMaxItemListId(): Flow<Long?>
}