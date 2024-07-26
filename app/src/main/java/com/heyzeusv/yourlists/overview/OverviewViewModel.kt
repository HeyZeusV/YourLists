package com.heyzeusv.yourlists.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.SettingsFilterOption
import com.heyzeusv.yourlists.database.CsvConverter
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.overview.OverviewFilterNames.BY_COMPLETION
import com.heyzeusv.yourlists.util.proto.SettingsManager
import com.heyzeusv.yourlists.util.proto.defaultSettingsFilter
import com.heyzeusv.yourlists.util.proto.getCustomSettingsDefaultInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val repo: Repository,
    private val csvConverter: CsvConverter,
) : ViewModel() {

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = getCustomSettingsDefaultInstance(),
    )

    private val byCompletionFilter
        get() = settings.value.overviewFilterList.find { it.name == BY_COMPLETION }
            ?: defaultSettingsFilter(BY_COMPLETION)

    val itemLists = settings
        .flatMapLatest { setting ->
            repo.getSortedItemListsWithItems(
                filter = OverviewFilter.settingsFilterToOverviewFilter(setting.overviewFilterList)
            )
        }
        .map { list ->
            if (byCompletionFilter.isSelected) {
                when (byCompletionFilter.filterOption) {
                    SettingsFilterOption.DESC -> list.sortedByDescending { it.progress.first }
                    else -> list.sortedBy { it.progress.first }
                }
            } else {
                list
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val nextItemListId = repo.getMaxItemListId()
        .map { (it ?: 0L) + 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )

    fun updateFilter(filter: OverviewFilter) {
        viewModelScope.launch {
            settingsManager.updateOverviewFilter(filter)
        }
    }

    suspend fun updatePortationPath(path: String) {
        settingsManager.updatePortationPath(path)
    }

    fun renameItemList(itemList: ItemList, newName: String) {
        viewModelScope.launch {
            val renamed = itemList.copy(name = newName)
            repo.updateItemList(renamed)
        }
    }

    fun insertItemList(name: String) {
        viewModelScope.launch {
            repo.insertItemList(ItemList(nextItemListId.value, name))
        }
    }

    fun copyItemList(itemList: ItemListWithItems) {
        viewModelScope.launch {
            val copyName = "${itemList.itemList.name} - Copy"
            val copyItemList = itemList.itemList.copy(
                itemListId = nextItemListId.value,
                name = copyName.take(32)
            )
            val copyItems = mutableListOf<Item>()
            itemList.items.forEach {
                copyItems.add(
                    it.copy(
                        itemId = 0L,
                        parentItemListId = nextItemListId.value
                    )
                )
            }

            repo.insertItemList(copyItemList)
            repo.insertItems(*copyItems.toTypedArray())
        }
    }

    fun deleteItemList(itemList: ItemList) {
        viewModelScope.launch {
            repo.deleteItemList(itemList)
        }
    }

    fun exportDatabaseToCsv() {
        viewModelScope.launch {
            val filePath = settings.value.portationPath
            val categoryData = repo.getAllCategories()
            val itemListData = repo.getAllItemLists()
            val defaultItemData = repo.getAllDefaultItems()
            val itemData = repo.getAllItems()
            csvConverter.exportDatabaseToCsv(filePath, categoryData, itemListData, defaultItemData, itemData)
        }
    }

    // TODO: Remove this
    // TODO: locate at data/user/0/com.heyzeusv.yourlists.files/...
}