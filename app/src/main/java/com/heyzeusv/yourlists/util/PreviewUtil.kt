package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.ui.theme.YourListsTheme

/**
 *  Helper object to create Compose Previews.
 */
object PreviewUtil {

    val itemUnchecked = Item(
        itemId = 0L,
        name = "UncheckedItem UncheckedItem UncheckedItem UncheckedItem UncheckedItem",
        isChecked = false,
        category = "Preview",
        quantity = 3.33,
        unit = "Unit",
        memo = "This is an unchecked item",
        parentItemListId = 0L,
        originItemListId = null
    )
    val itemChecked = Item(
        itemId = 0L,
        name = "CheckedItem",
        isChecked = true,
        category = "Preview",
        quantity = 3.33,
        unit = "Units",
        memo = "This is a checked item",
        parentItemListId = 0L,
        originItemListId = null
    )
    val halfCheckedItemList = ItemListWithItems(
        itemList = ItemList(0L, "Half Checked / Half UnChecked Item List"),
        items = List(10) { if (it % 2 == 0) itemChecked else itemUnchecked }
    )
    val emptyItemList = ItemListWithItems(
        itemList = ItemList(0L, "Empty List"),
        items = emptyList()
    )
    val defaultItem = DefaultItem(
        itemId = 0L,
        name = "DefaultItem DefaultItem DefaultItem DefaultItem DefaultItem",
        category = "Preview",
        quantity = 3.33,
        unit = "Unit",
        memo = "This is a default item",
        )
    val defaultItemList = List(10) { defaultItem }

    @Composable
    fun Preview(content: @Composable () -> Unit) {
        YourListsTheme {
            Box(modifier = Modifier.fillMaxWidth())  {
                content()
            }
        }
    }
}