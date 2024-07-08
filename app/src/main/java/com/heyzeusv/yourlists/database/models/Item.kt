package com.heyzeusv.yourlists.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

interface BaseItem {
    val itemId: Long
    val name: String
    val category: String
    val quantity: Double
    val unit: String
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["name"],
            childColumns = ["category"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemList::class,
            parentColumns = ["itemListId"],
            childColumns = ["parentItemListId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["category"],
            name = "index_category_name"
        ),
        Index(
            value = ["parentItemListId"],
            name = "index_parent_id"
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    override val itemId: Long,
    override val name: String,
    val isChecked: Boolean,
    override val category: String,
    override val quantity: Double,
    override val unit: String,
    val parentItemListId: Long,
    val originItemListId: Long?
) : BaseItem

@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["name"],
        childColumns = ["category"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["category"],
        name = "index_default_category_name"
    )]
)
data class DefaultItem(
    @PrimaryKey(autoGenerate = true)
    override val itemId: Long,
    override val name: String,
    override val category: String,
    override val quantity: Double,
    override val unit: String
) : BaseItem

@Fts4(contentEntity = DefaultItem::class)
@Entity
data class DefaultItemFts(
    val name: String,
    val category: String
)