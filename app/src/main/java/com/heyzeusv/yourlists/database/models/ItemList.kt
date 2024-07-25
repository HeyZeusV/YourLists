package com.heyzeusv.yourlists.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.heyzeusv.yourlists.database.DatabaseEntity

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true)
    val itemListId: Long = 0L,
    val name: String = "",
) : DatabaseEntity {
    @Ignore
    override val csvName: String = this::class.java.simpleName
    @Ignore
    override val csvHeader: List<String> = listOf(::itemListId.name, ::name.name)
    @Ignore
    override val csvRow: List<Any> = listOf(itemListId, name)
}

data class ItemListWithItems(
    @Embedded val itemList: ItemList = ItemList(0, ""),
    @Relation(
        parentColumn = "itemListId",
        entityColumn = "parentItemListId"
    )
    val items: List<Item> = emptyList()
) {
    @Ignore
    private val numOfCheckedItems = items.count { it.isChecked }
    @Ignore
    private val numOfItems = items.size
    @Ignore
    private val progressFloat = if (numOfItems == 0) {
        0f
    } else {
        numOfCheckedItems.toFloat() / numOfItems
    }
    @Ignore
    private val progressString = "$numOfCheckedItems/$numOfItems"
    @Ignore
    val progress = Pair(progressFloat, progressString)
}