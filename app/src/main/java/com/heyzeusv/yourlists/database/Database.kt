package com.heyzeusv.yourlists.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heyzeusv.yourlists.database.dao.AllDao
import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.DefaultItemDao
import com.heyzeusv.yourlists.database.dao.ItemDao
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
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class Database : RoomDatabase() {
    abstract fun allDao(): AllDao
    abstract fun itemListDao(): ItemListDao
    abstract fun itemDao(): ItemDao
    abstract fun defaultItemDao(): DefaultItemDao
    abstract fun categoryDao(): CategoryDao
}