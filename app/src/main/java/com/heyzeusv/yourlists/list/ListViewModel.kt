package com.heyzeusv.yourlists.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    private val _itemList = MutableStateFlow(ItemListWithItems())
    val itemList = _itemList.asStateFlow()

    fun insertItemList(name: String) {
        viewModelScope.launch {
            _itemList.update { it.copy(itemList = ItemList(0L, name)) }
            val id = repo.insertItemList(_itemList.value.itemList)
            _itemList.update { it.copy(itemList = ItemList(id, name)) }
        }
    }

    fun getItemListWithId(id: Long) {
        viewModelScope.launch {
            _itemList.update { repo.getItemListWithId(id) ?: ItemListWithItems() }
        }
    }
}