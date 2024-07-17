package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg entities: T): List<Long>

    @Update
    suspend fun update(vararg entities: T)

    @Delete
    suspend fun delete(vararg entities: T)

    @Upsert
    suspend fun upsert(vararg entities: T)
}