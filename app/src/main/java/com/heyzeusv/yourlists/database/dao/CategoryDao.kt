package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.heyzeusv.yourlists.database.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<Category> {

    @Query("SELECT * " +
            "FROM Category " +
            "ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
}