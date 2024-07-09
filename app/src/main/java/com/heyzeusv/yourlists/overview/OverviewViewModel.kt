package com.heyzeusv.yourlists.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
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

    private fun getAllItemLists() {
        viewModelScope.launch {
            repo.getAllItemListsWithItems().flowOn(Dispatchers.IO).collectLatest { lists ->
                _itemLists.update { lists }
            }
        }
    }
}