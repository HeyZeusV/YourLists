package com.heyzeusv.yourlists.overview

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.heyzeusv.yourlists.SettingsFilterOption
import com.heyzeusv.yourlists.util.portation.CsvConverter
import com.heyzeusv.yourlists.database.Database
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.di.IODispatcher
import com.heyzeusv.yourlists.overview.OverviewFilterNames.BY_COMPLETION
import com.heyzeusv.yourlists.util.ListOptions
import com.heyzeusv.yourlists.util.ListOptions.Copy
import com.heyzeusv.yourlists.util.ListOptions.Delete
import com.heyzeusv.yourlists.util.ListOptions.Rename
import com.heyzeusv.yourlists.util.portation.PortationStatus
import com.heyzeusv.yourlists.util.portation.PortationStatus.Error
import com.heyzeusv.yourlists.util.portation.PortationStatus.Progress
import com.heyzeusv.yourlists.util.portation.PortationStatus.Standby
import com.heyzeusv.yourlists.util.proto.SettingsManager
import com.heyzeusv.yourlists.util.proto.defaultSettingsFilter
import com.heyzeusv.yourlists.util.proto.getCustomSettingsDefaultInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OverviewViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val settingsManager: SettingsManager,
    private val database: Database,
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

    private val _portationStatus = MutableStateFlow<PortationStatus>(Standby)
    val portationStatus = _portationStatus.asStateFlow()
    fun updatePortationStatus(value: PortationStatus) = _portationStatus.update { value }

    fun updateFilter(filter: OverviewFilter) {
        viewModelScope.launch {
            settingsManager.updateOverviewFilter(filter)
        }
    }

    suspend fun updatePortationPath(path: String) {
        settingsManager.updatePortationPath(path)
    }

    fun insertItemList(name: String) {
        viewModelScope.launch {
            repo.insertItemList(ItemList(nextItemListId.value, name))
        }
    }

    fun handleListOption(option: ListOptions) {
        viewModelScope.launch {
            when (option) {
                is Rename -> renameItemList(option.itemList.itemList, option.newName)
                is Copy -> copyItemList(option)
                is Delete -> deleteItemList(option.itemList.itemList)
            }
        }
    }

    private suspend fun renameItemList(itemList: ItemList, newName: String) {
        val renamed = itemList.copy(name = newName)
        repo.updateItemList(renamed)
    }

    private suspend fun copyItemList(copyOption: Copy) {
        val copyName = "${copyOption.itemList.itemList.name} - Copy"
        val copyItemList = copyOption.itemList.itemList.copy(
            itemListId = nextItemListId.value,
            name = copyName.take(32)
        )
        val copyItems: List<Item> = copyOption.copyItems()
        val copyItemsEdited = copyItems.map {
            it.copy(
                itemId = 0L,
                parentItemListId = nextItemListId.value
            )
        }

        repo.copyItemListWithItems(copyItemList, copyItemsEdited)
    }

    private suspend fun deleteItemList(itemList: ItemList) = repo.deleteItemList(itemList)

    fun importCsvToDatabase(selectedDirectoryUri: Uri) {
        viewModelScope.launch(ioDispatcher) {
            val result = csvConverter.importCsvToDatabase(
                selectedDirectoryUri = selectedDirectoryUri,
                updatePortationStatus = { status -> _portationStatus.update { status } },
            )
            result?.let {
                _portationStatus.update { Progress.ImportUpdateDatabase }
                database.withTransaction {
                    repo.deleteAll()
                    repo.insertCsvData(result)
                    repo.rebuildDefaultItemFts()
                    _portationStatus.update { Progress.ImportSuccess }
                }
            }
        }
    }

    fun createParentDirectoryAndExportToCsv(selectedDirectoryUri: Uri) {
        viewModelScope.launch(ioDispatcher) {
            val parentDirectoryUri = csvConverter.findOrCreateParentDirectory(selectedDirectoryUri)
            if (parentDirectoryUri == null) {
                updatePortationStatus(Error.CreateDirectoryFailed)
            } else {
                updatePortationPath(parentDirectoryUri.toString())
                suspendExportDatabaseToCsv()
            }
        }
    }

    fun exportDatabaseToCsv() {
        viewModelScope.launch(ioDispatcher) {
            suspendExportDatabaseToCsv()
        }
    }

    private suspend fun suspendExportDatabaseToCsv() {
        val parentDirectoryUri = Uri.parse(settings.value.portationPath)
        val csvData = repo.getAllCsvData()

        csvConverter.exportDatabaseToCsv(
            parentDirectoryUri = parentDirectoryUri,
            csvData = csvData,
            updatePortationStatus = { status -> _portationStatus.update { status } },
        )
    }
}