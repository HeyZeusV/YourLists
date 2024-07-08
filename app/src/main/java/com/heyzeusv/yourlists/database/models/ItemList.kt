package com.heyzeusv.yourlists.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
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
)