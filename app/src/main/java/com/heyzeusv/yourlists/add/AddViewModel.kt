package com.heyzeusv.yourlists.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.ListOptions.Copy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: Repository,
) : ViewModel() {

    private val itemListId = savedStateHandle.getStateFlow(AddDestination.ID_ARG, 0L)

    private val _defaultItemQuery = MutableStateFlow("")
    val defaultItemQuery = _defaultItemQuery.asStateFlow()
    fun updateDefaultItemQuery(query: String) { _defaultItemQuery.update { query } }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val defaultItems = _defaultItemQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repo.getAllDefaultItems()
            } else {
                val cleanQuery = cleanQuery(query)
                repo.searchDefaultItems(cleanQuery)
            }
        }

    val categories = repo.getAllCategoriesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemLists = itemListId
        .flatMapLatest { id -> repo.getAllItemListsWithoutId(id) }
        .map { allList ->
            allList.filter { itemList ->
                itemList.items.isNotEmpty() &&
                !itemList.items.any { item -> item.originItemListId != null }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    private fun cleanQuery(query: String): String {
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "\"*$queryWithEscapedQuotes*\""
    }

    fun saveDefaultItemAndAddItem(defaultItem: DefaultItem) {
        viewModelScope.launch {
            if (categories.value.find { it.name == defaultItem.category } == null) {
                repo.insertCategories(Category(id = 0L, name = defaultItem.category))
            }
            repo.upsertDefaultItems(defaultItem)
            repo.insertItems(defaultItem.toItem(itemListId.value))
        }
    }

    fun addItem(defaultItem: DefaultItem) {
        viewModelScope.launch {
            if (categories.value.find { it.name == defaultItem.category } == null) {
                repo.insertCategories(Category(id = 0L, name = defaultItem.category))
            }
            repo.insertItems(defaultItem.toItem(itemListId.value))
        }
    }

    fun deleteDefaultItem(defaultItem: DefaultItem) {
        viewModelScope.launch { repo.deleteDefaultItems(defaultItem) }
    }

    fun addListWithOption(option: Copy) {
        viewModelScope.launch {
            val itemsToAdd: List<Item> = option.copyItems()
            val itemsToAddEdited = itemsToAdd.map {
                it.copy(
                    itemId = 0L,
                    parentItemListId = itemListId.value,
                    originItemListId = option.itemList.itemList.itemListId
                )
            }
            repo.insertItems(*itemsToAddEdited.toTypedArray())
        }
    }
}