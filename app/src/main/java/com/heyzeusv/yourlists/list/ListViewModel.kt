package com.heyzeusv.yourlists.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.proto.SettingsManager
import com.heyzeusv.yourlists.util.proto.getCustomSettingsDefaultInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsManager: SettingsManager,
    private val repo: Repository,
) : ViewModel() {

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = getCustomSettingsDefaultInstance(),
    )

    private val itemListId = savedStateHandle.getStateFlow(ListDestination.ID_ARG, 0L)

    val itemList = itemListId
        .flatMapLatest { id -> repo.getItemListWithId(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ItemList(itemListId = 0L, name = "")
        )

    val items = settings
        .flatMapLatest { setting ->
            repo.getSortedItemsWithParentId(
                id = itemListId.value,
                filter = ListFilter.settingsFilterToListFilter(setting.listFilterList)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories = _categories.asStateFlow()

    init {
        getAllCategories()
    }

    fun updateFilter(filter: ListFilter) {
        viewModelScope.launch {
            settingsManager.updateListFilter(filter)
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            repo.getAllCategoriesFlow().flowOn(Dispatchers.IO).collectLatest { list ->
                _categories.update { list }
            }
        }
    }

    fun updateItemIsChecked(
        item: Item,
        updateCheckboxEnabled: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            updateCheckboxEnabled(false)
            repo.updateItems(item.copy(isChecked = !item.isChecked))
            updateCheckboxEnabled(true)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            if (_categories.value.firstOrNull { it.name == item.category } == null) {
                repo.insertCategories(Category(id = 0L, name = item.category))
            }
            repo.updateItems(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch { repo.deleteItems(item) }
    }
}