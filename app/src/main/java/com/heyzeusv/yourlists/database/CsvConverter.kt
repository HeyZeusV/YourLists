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
        filePath: String,
        categoryData: List<Category>,
        itemListData: List<ItemList>,
        defaultItemData: List<DefaultItem>,
        itemData: List<Item>,
    ) {
        exportDatabaseEntityToCsv(filePath, Category(), categoryData)
        exportDatabaseEntityToCsv(filePath, ItemList(), itemListData)
        exportDatabaseEntityToCsv(filePath, DefaultItem(), defaultItemData)
        exportDatabaseEntityToCsv(filePath, Item(), itemData)
    }

    private fun exportDatabaseEntityToCsv(
        filePath: String,
        entity: DatabaseEntity,
        entityData: List<DatabaseEntity>,
    ) {
        val csvFile = createFile(filePath, entity.csvName)
        csvWriter().open(targetFile = csvFile, append = false) {
            writeRow(entity.csvHeader)
            entityData.forEach {
                writeRow(it.csvRow)
            }
        }
    }

    private fun createFile(filePath: String, fileName: String): File {
        return File(context.filesDir, "$fileName$CSV_SUFFIX").apply {
            delete()
            createNewFile()
        }
    }
}