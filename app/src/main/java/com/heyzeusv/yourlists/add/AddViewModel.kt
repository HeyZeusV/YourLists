package com.heyzeusv.yourlists.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyzeusv.yourlists.database.Repository
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

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

    private val _categories = MutableStateFlow(emptyList<String>())
    val categories = _categories.asStateFlow()

    private val _itemLists = MutableStateFlow(emptyList<ItemListWithItems>())
    val itemLists = _itemLists.asStateFlow()

    init {
        getAllItemLists()
        getAllCategories()
    }

    private fun cleanQuery(query: String): String {
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), ("\"\""))
        val verbatimQuery = "*\"$queryWithEscapedQuotes\"*"
        return verbatimQuery
    }

    private fun getAllItemLists() {
        viewModelScope.launch {
            repo.getAllItemLists().flowOn(Dispatchers.IO).collectLatest { lists ->
                _itemLists.update { lists }
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
}