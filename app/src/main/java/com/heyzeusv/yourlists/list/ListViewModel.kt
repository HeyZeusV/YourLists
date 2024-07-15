package com.heyzeusv.yourlists.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.ListDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: Repository,
) : ViewModel() {

    private val _itemList = MutableStateFlow(ItemListWithItems(ItemList(-1L, "")))
    val itemList = _itemList.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories = _categories.asStateFlow()

    init {
        getItemListWithId(checkNotNull(savedStateHandle[ListDestination.ID_ARG]))
        getAllCategories()
    }

    fun insertItemList(name: String) {
        viewModelScope.launch {
            _itemList.update { it.copy(itemList = ItemList(0L, name)) }
            val id = repo.insertItemList(_itemList.value.itemList)
            _itemList.update { it.copy(itemList = ItemList(id, name)) }
        }
    }

    private fun getItemListWithId(id: Long) {
        viewModelScope.launch {
            repo.getItemListWithId(id).flowOn(Dispatchers.IO).collectLatest { list ->
                _itemList.update { list ?: ItemListWithItems() }
            }
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            repo.getAllCategories().flowOn(Dispatchers.IO).collectLatest { list ->
                _categories.update { list }
            }
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