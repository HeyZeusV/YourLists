package com.heyzeusv.yourlists.util

import com.heyzeusv.yourlists.database.models.ItemListWithItems

sealed class ListOptions {
    abstract val itemList: ItemListWithItems

    data class Rename(override val itemList: ItemListWithItems, val newName: String): ListOptions()
    sealed class Copy: ListOptions() {
        data class AllAsUnchecked(override val itemList: ItemListWithItems): Copy()
        data class AllAsIs(override val itemList: ItemListWithItems): Copy()
        data class OnlyUnchecked(override val itemList: ItemListWithItems): Copy()
    }
    data class Delete(override val itemList: ItemListWithItems): ListOptions()
}