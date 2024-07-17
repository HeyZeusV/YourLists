package com.heyzeusv.yourlists.database.dao

import androidx.room.Dao
import com.heyzeusv.yourlists.database.models.Item

@Dao
interface ItemDao : BaseDao<Item> {

}