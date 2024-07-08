package com.heyzeusv.yourlists.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heyzeusv.yourlists.database.dao.ItemListDao
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.DefaultItemFts
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList

@Database(
    entities = [
        DefaultItem::class,
        DefaultItemFts::class,
        Item::class,
        ItemList::class,
        Category::class
    ],
    version = 1,
    exportSchema = true
)
abstract class Database : RoomDatabase() {

    abstract fun itemListDao(): ItemListDao
}