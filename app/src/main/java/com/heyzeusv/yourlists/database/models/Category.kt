package com.heyzeusv.yourlists.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heyzeusv.yourlists.util.portation.CsvInfo

@Entity(
    indices = [Index(
        value = ["name"],
        name = "index_category_name",
        unique = true,
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
) : CsvInfo {
    @Ignore
    override val csvName: String = this::class.java.simpleName
    @Ignore
    override val csvHeader: List<String> = listOf(::id.name, ::name.name)
    @Ignore
    override val csvRow: List<Any> = listOf(id, name)
}