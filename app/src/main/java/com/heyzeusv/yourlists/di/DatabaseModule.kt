package com.heyzeusv.yourlists.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.heyzeusv.yourlists.database.Database
import com.heyzeusv.yourlists.database.dao.AllDao
import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.DefaultItemDao
import com.heyzeusv.yourlists.database.dao.ItemDao
import com.heyzeusv.yourlists.database.dao.ItemListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideAllDao(database: Database): AllDao = database.allDao()

    @Provides
    fun provideItemListDao(database: Database): ItemListDao = database.itemListDao()

    @Provides
    fun provideItemDao(database: Database): ItemDao = database.itemDao()

    @Provides
    fun provideDefaultItemDao(database: Database): DefaultItemDao = database.defaultItemDao()

    @Provides
    fun provideCategoryDao(database: Database): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "YourListsDatabase.db"
        )
            .createFromAsset("YourListsInitDatabase.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO DefaultItemFts(DefaultItemFts) VALUES ('rebuild')")
                }
            })
            .build()
    }
}