package com.heyzeusv.yourlists.database

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import java.io.File

private const val CSV_SUFFIX = ".csv"

class CsvConverter(
    private val context: Context,
) {
    fun exportDatabaseEntityToCsv(
        csvFile: File,
        entity: DatabaseEntity,
        entityData: List<DatabaseEntity>,
    ) {
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