package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.heyzeusv.yourlists.database.models.Category

@Dao
interface CategoryDao {

    @Query("SELECT * " +
            "FROM Category")
    fun getAllCategories(): List<Category>
}