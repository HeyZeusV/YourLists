package com.heyzeusv.yourlists.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
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
class OverviewViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    private val _itemLists = MutableStateFlow(emptyList<ItemListWithItems>())
    val itemLists = _itemLists.asStateFlow()

    init {
        getAllItemLists()
    }

    fun renameItemList(itemList: ItemList, newName: String) {
        viewModelScope.launch {
            val renamed = itemList.copy(name = newName)
            repo.updateItemList(renamed)
        }
    }

    fun copyItemList(itemList: ItemListWithItems) {
        viewModelScope.launch {
            val copyName = "${itemList.itemList.name} - Copy"
            val copy = itemList.itemList.copy(
                itemListId = 0L,
                name = copyName.take(32)
            )
            repo.insertItemList(copy)
            // TODO: copy items once DAO is set up for it
        }
    }

    fun deleteItemList(itemList: ItemList) {
        viewModelScope.launch {
            repo.deleteItemList(itemList)
        }
    }

    private fun getAllItemLists() {
        viewModelScope.launch {
            repo.getAllItemLists().flowOn(Dispatchers.IO).collectLatest { lists ->
                _itemLists.update { lists }
            }
        }
    }
}