package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT name " +
            "FROM Category")
    fun getAllCategories(): Flow<List<String>>
}