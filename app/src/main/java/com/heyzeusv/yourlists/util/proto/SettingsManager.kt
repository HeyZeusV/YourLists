package com.heyzeusv.yourlists.util.proto

import android.util.Log
import androidx.datastore.core.DataStore
import com.heyzeusv.yourlists.Settings
import com.heyzeusv.yourlists.SettingsFilter
import com.heyzeusv.yourlists.list.ListFilter
import com.heyzeusv.yourlists.list.ListFilterNames
import com.heyzeusv.yourlists.overview.OverviewFilter
import com.heyzeusv.yourlists.overview.OverviewFilterNames
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

    suspend fun updateOverviewFilter(overviewFilter: OverviewFilter) {
        val byCompletionFilter = SettingsFilter
            .newBuilder()
            .setName(OverviewFilterNames.BY_COMPLETION)
            .setIsSelected(overviewFilter.byCompletion)
            .setFilterOption(overviewFilter.byCompletionOption.sFilterOption)
            .build()
        val byNameFilter = SettingsFilter
            .newBuilder()
            .setName(OverviewFilterNames.BY_NAME)
            .setIsSelected(overviewFilter.byName)
            .setFilterOption(overviewFilter.byNameOption.sFilterOption)
            .build()
        val filterList = listOf(byCompletionFilter, byNameFilter)
        settings.updateData { settings ->
            settings
                .toBuilder()
                .clearOverviewFilter()
                .addAllOverviewFilter(filterList)
                .build()
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
                .clearListFilter()
                .addAllListFilter(filterList)
                .build()
        }
    }

    suspend fun updatePortationPath(path: String) {
        settings.updateData { settings ->
            settings
                .toBuilder()
                .setPortationPath(path)
                .build()
        }
    }
}

fun getCustomSettingsDefaultInstance(): Settings = Settings
    .newBuilder()
    .addAllOverviewFilter(defaultOverviewFilter())
    .addAllListFilter(defaultListFilter())
    .build()

fun defaultSettingsFilter(name: String): SettingsFilter =
    SettingsFilter.newBuilder().setName(name).build()

private fun defaultOverviewFilter(): List<SettingsFilter> {
    val filters = mutableListOf<SettingsFilter>()
    OverviewFilterNames.names.forEach {
        filters.add(defaultSettingsFilter(it))
    }
    return filters
}

private fun defaultListFilter(): List<SettingsFilter> {
    val filters = mutableListOf<SettingsFilter>()
    ListFilterNames.names.forEach {
        filters.add(defaultSettingsFilter(it))
    }
    return filters
}