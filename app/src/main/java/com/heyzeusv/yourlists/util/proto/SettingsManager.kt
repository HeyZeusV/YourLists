package com.heyzeusv.yourlists.util.proto

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.yourlists.Settings
import com.heyzeusv.yourlists.SettingsFilter
import com.heyzeusv.yourlists.list.ListFilter
import com.heyzeusv.yourlists.list.ListFilterNames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val settings: DataStore<Settings>
) {
    val settingsFlow: Flow<Settings> = settings.data
        .catch { exception ->
            // DataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Your Lists", "Error reading settings.", exception)
                emit(getCustomSettingsDefaultInstance())
            } else {
                throw exception
            }
        }
    suspend fun updateListFilter(listFilter: ListFilter) {
        val byIsCheckedFilter = SettingsFilter
            .newBuilder()
            .setName(ListFilterNames.BY_IS_CHECKED)
            .setIsSelected(listFilter.byIsChecked)
            .setFilterOption(listFilter.byIsCheckedOption.sFilterOption)
            .build()
        val byCategoryFilter = SettingsFilter
            .newBuilder()
            .setName(ListFilterNames.BY_CATEGORY)
            .setIsSelected(listFilter.byCategory)
            .setFilterOption(listFilter.byCategoryOption.sFilterOption)
            .build()
        val byNameFilter = SettingsFilter
            .newBuilder()
            .setName(ListFilterNames.BY_NAME)
            .setIsSelected(listFilter.byName)
            .setFilterOption(listFilter.byNameOption.sFilterOption)
            .build()
        val filterList = listOf(byIsCheckedFilter, byCategoryFilter, byNameFilter)
        settings.updateData { settings ->
            settings
                .toBuilder()
                .clearListFilters()
                .addAllListFilters(filterList)
                .build()
        }
    }
}

fun getCustomSettingsDefaultInstance(): Settings = Settings
    .newBuilder()
    .addAllListFilters(defaultListFilters())
    .build()

private fun defaultListFilters(): List<SettingsFilter> {
    val filters = mutableListOf<SettingsFilter>()
    ListFilterNames.names.forEach {
        val filter = SettingsFilter.newBuilder().setName(it).build()
        filters.add(filter)
    }
    return filters
}