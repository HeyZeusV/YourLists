package com.heyzeusv.yourlists.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true)
    val itemListId: Long,
    val name: String
)

data class ItemListWithItems(
    @Embedded val itemList: ItemList,
    @Relation(
        parentColumn = "itemListId",
        entityColumn = "parentItemListId"
    )
    val items: List<Item>
) {
    @Ignore private val numOfCheckedItems = items.count { it.isChecked }
    @Ignore private val numOfItems = items.size
    @Ignore private val progressFloat = numOfCheckedItems.toFloat() / numOfItems
    @Ignore private val progressString = "$numOfCheckedItems/$numOfItems"
    @Ignore val progress = Pair(progressFloat, progressString)
}