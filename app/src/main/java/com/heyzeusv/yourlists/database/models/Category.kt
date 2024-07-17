package com.heyzeusv.yourlists.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(
        value = ["name"],
        name = "index_category_name",
        unique = true,
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
)
