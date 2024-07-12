package com.heyzeusv.yourlists.di

import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.RepositoryImpl
import com.heyzeusv.yourlists.database.dao.CategoryDao
import com.heyzeusv.yourlists.database.dao.DefaultItemDao
import com.heyzeusv.yourlists.database.dao.ItemListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        itemListDao: ItemListDao,
        defaultItemDao: DefaultItemDao,
        categoryDao: CategoryDao,
    ): Repository = RepositoryImpl(itemListDao, defaultItemDao, categoryDao)
}