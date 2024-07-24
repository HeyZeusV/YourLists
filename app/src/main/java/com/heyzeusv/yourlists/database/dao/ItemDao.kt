package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.util.FilterValue.ASC
import com.heyzeusv.yourlists.util.FilterValue.DESC
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao : BaseDao<Item> {

    @Query("SELECT * " +
            "FROM Item " +
            "WHERE parentItemListId=(:id) " +
            "ORDER BY " +
            "CASE WHEN :byIsChecked = 1 AND :byIsCheckedOption = '$ASC' THEN isChecked END ASC, " +
            "CASE WHEN :byIsChecked = 1 AND :byIsCheckedOption = '$DESC' THEN isChecked END DESC, " +
            "CASE WHEN :byCategory = 1 AND :byCategoryOption = '$ASC' THEN category END ASC, " +
            "CASE WHEN :byCategory = 1 AND :byCategoryOption = '$DESC' THEN category END DESC," +
            "CASE WHEN :byName = 1 AND :byNameOption = '$ASC' THEN name END ASC, " +
            "CASE WHEN :byName = 1 AND :byNameOption = '$DESC' THEN name END DESC")
    fun getSortedItemsWithParentId(
        id: Long,
        byIsChecked: Boolean,
        byIsCheckedOption: String,
        byCategory: Boolean,
        byCategoryOption: String,
        byName: Boolean,
        byNameOption: String,
    ): Flow<List<Item>>
}