package com.heyzeusv.yourlists.util

import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemListWithItems

sealed class ListOptions {
    abstract val itemList: ItemListWithItems

    data class Rename(override val itemList: ItemListWithItems, val newName: String): ListOptions()
    sealed class Copy: ListOptions() {
        data class AllAsUnchecked(override val itemList: ItemListWithItems): Copy()
        data class AllAsIs(override val itemList: ItemListWithItems): Copy()
        data class OnlyUnchecked(override val itemList: ItemListWithItems): Copy()

        fun copyItems(): List<Item> {
            return when (this) {
                is AllAsUnchecked -> itemList.items.map { it.copy(isChecked = false) }
                is AllAsIs -> itemList.items
                is OnlyUnchecked -> itemList.items.filter { !it.isChecked }
            }
        }
    }
    data class Delete(override val itemList: ItemListWithItems): ListOptions()
}