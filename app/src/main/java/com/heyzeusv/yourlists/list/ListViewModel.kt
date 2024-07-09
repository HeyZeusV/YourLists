package com.heyzeusv.yourlists.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
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

    fun getItemListWithId(id: Long) {
        viewModelScope.launch {
            _itemList.update { repo.getItemListWithId(id) ?: ItemListWithItems() }
        }
    }
}