package com.heyzeusv.yourlists.util.proto

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.yourlists.Settings
import com.heyzeusv.yourlists.SettingsFilter
import com.heyzeusv.yourlists.list.ListFilter
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
                emit(Settings.getDefaultInstance())
            } else {
                throw exception
            }
        }
    suspend fun updateListFilters(listFilter: ListFilter) {
        val byIsCheckedFilter = SettingsFilter.newBuilder()
            .setName("byIsChecked")
            .setIsSelected(listFilter.byIsChecked)
            .setFilterOption(listFilter.byIsCheckedOption.sFilterOption)
            .build()
        val byNameFilter = SettingsFilter.newBuilder()
            .setName("byName")
            .setIsSelected(listFilter.byName)
            .setFilterOption(listFilter.byNameOption.sFilterOption)
            .build()
        val byCategoryFilter = SettingsFilter.newBuilder()
            .setName("byCategory")
            .setIsSelected(listFilter.byCategory)
            .setFilterOption(listFilter.byCategoryOption.sFilterOption)
            .build()
        val filterList = listOf(byIsCheckedFilter, byNameFilter, byCategoryFilter)
        settings.updateData { settings ->
            val builder = settings.toBuilder()
            builder.clearListFilters()
            builder.addAllListFilters(filterList)
            builder.build()
        }
    }
}