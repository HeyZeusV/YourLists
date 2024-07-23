package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.util.FilterValue
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao : BaseDao<Item> {

    @Query("SELECT * " +
            "FROM Item " +
            "WHERE parentItemListId=(:id) " +
            "ORDER BY " +
            "CASE WHEN :byIsChecked = '${FilterValue.ASC}' THEN isChecked END ASC, " +
            "CASE WHEN :byIsChecked = '${FilterValue.DESC}' THEN isChecked END DESC , " +
            "CASE WHEN :byName = '${FilterValue.ASC}' THEN name END ASC, " +
            "CASE WHEN :byName = '${FilterValue.DESC}' THEN name END DESC , " +
            "CASE WHEN :byCategory = '${FilterValue.ASC}' THEN category END ASC, " +
            "CASE WHEN :byCategory = '${FilterValue.DESC}' THEN category END DESC")
    fun getSortedItemsWithParentId(
        id: Long,
        byIsChecked: String,
        byName: String,
        byCategory: String
    ): Flow<List<Item>>
}