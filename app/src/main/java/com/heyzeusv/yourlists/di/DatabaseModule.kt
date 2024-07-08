package com.heyzeusv.yourlists.di

import android.content.Context
import androidx.room.Room
import com.heyzeusv.yourlists.database.AppDatabase
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
    fun provideItemListDao(database: AppDatabase): ItemListDao = database.itemListDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "yourListsDatabase"
        )
            .addMigrations()
            .build()
    }
}