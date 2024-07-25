package com.heyzeusv.yourlists.database

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import java.io.File
import javax.inject.Inject

private const val CSV_SUFFIX = ".csv"

class CsvConverter @Inject constructor(
    private val context: Context,
) {
    fun exportDatabaseToCsv(
        categoryData: List<Category>,
        itemListData: List<ItemList>,
        defaultItemData: List<DefaultItem>,
        itemData: List<Item>,
    ) {
        exportDatabaseEntityToCsv(Category(), categoryData)
        exportDatabaseEntityToCsv(ItemList(), itemListData)
        exportDatabaseEntityToCsv(DefaultItem(), defaultItemData)
        exportDatabaseEntityToCsv(Item(), itemData)
    }

    private fun exportDatabaseEntityToCsv(
        entity: DatabaseEntity,
        entityData: List<DatabaseEntity>,
    ) {
        val csvFile = createFile(entity.csvName)
        csvWriter().open(targetFile = csvFile, append = false) {
            writeRow(entity.csvHeader)
            entityData.forEach {
                writeRow(it.csvRow)
            }
        }
    }

    private fun createFile(fileName: String): File {
        return File(context.filesDir, "$fileName$CSV_SUFFIX").apply {
            delete()
            createNewFile()
        }
    }
}