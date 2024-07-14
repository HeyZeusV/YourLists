package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg entities: T): List<Long>

    @Update
    suspend fun update(vararg entities: T)

    @Delete
    suspend fun delete(vararg entities: T)

    @Transaction
    suspend fun upsert(vararg entities: T) {
        val insertResult: List<Long> = insert(*entities)
        val updateList = mutableListOf<T>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(entities[i])
        }

        updateList.forEach { update(it) }
    }
}