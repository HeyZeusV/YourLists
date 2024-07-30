package com.heyzeusv.yourlists.database

import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList

data class DatabaseData(
    val categoryData: List<Category>,
    val itemListData: List<ItemList>,
    val defaultItemData : List<DefaultItem>,
    val itemData: List<Item>,
) {
    val entityDataPair: List<Pair<DatabaseEntity, List<DatabaseEntity>>> = listOf(
        Pair(Category(), categoryData),
        Pair(ItemList(), itemListData),
        Pair(DefaultItem(), defaultItemData),
        Pair(Item(), itemData),
    )

    override fun toString(): String {
        var string = ""
        string += "~~~~~Category Data~~~~~\n"
        categoryData.forEach { string += "$it\n"}
        string += "~~~~~Item List Data~~~~~\n"
        itemListData.forEach { string += "$it\n"}
        string += "~~~~~Default Item Data~~~~~\n"
        defaultItemData.forEach { string += "$it\n"}
        string += "~~~~~Item Data~~~~~\n"
        itemData.forEach { string += "$it\n"}
        return string
    }
}