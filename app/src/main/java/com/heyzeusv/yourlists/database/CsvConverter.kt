package com.heyzeusv.yourlists.database

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import javax.inject.Inject

private const val PARENT_DIRECTORY_NAME = "YourLists"
private const val CSV_SUFFIX = ".csv"

class CsvConverter @Inject constructor(
    private val context: Context,
) {
    fun exportDatabaseToCsv(
        parentDirectoryUri: Uri,
        categoryData: List<Category>,
        itemListData: List<ItemList>,
        defaultItemData: List<DefaultItem>,
        itemData: List<Item>,
    ) {
        val thisExportDirectory = createThisExportDirectory(parentDirectoryUri)
        exportDatabaseEntityToCsv(thisExportDirectory, Category(), categoryData)
        exportDatabaseEntityToCsv(thisExportDirectory, ItemList(), itemListData)
        exportDatabaseEntityToCsv(thisExportDirectory, DefaultItem(), defaultItemData)
        exportDatabaseEntityToCsv(thisExportDirectory, Item(), itemData)
    }

    private fun exportDatabaseEntityToCsv(
        thisExportDirectory: DocumentFile,
        entity: DatabaseEntity,
        entityData: List<DatabaseEntity>,
    ) {
        val csvFile = createAndWriteEntityDataToFile(entity, entityData)
        csvWriter().open(targetFile = csvFile, append = false) {
            writeRow(entity.csvHeader)
            entityData.forEach {
                writeRow(it.csvRow)
            }
        }
        val csvDocumentFile =
            thisExportDirectory.createFile("text/*", "${entity.csvName}$CSV_SUFFIX")!!
        writeToDocumentFileFromFile(csvFile, csvDocumentFile)
    }

    private fun createAndWriteEntityDataToFile(
        entity: DatabaseEntity,
        entityData: List<DatabaseEntity>,
    ): File {
        val csvFile = File(context.filesDir, "${entity.csvName}$CSV_SUFFIX").apply {
            delete()
            createNewFile()
        }
        csvWriter().open(csvFile) {
            writeRow(entity.csvHeader)
            entityData.forEach {
                writeRow(it.csvRow)
            }
        }
        return csvFile
    }

    private fun writeToDocumentFileFromFile(csvFile: File, csvDocumentFile: DocumentFile) {
        val fInputStream = FileInputStream(csvFile)
        val fReader = BufferedReader(InputStreamReader(fInputStream))

        val dfFileDescriptor = context.contentResolver.openFileDescriptor(
            Objects.requireNonNull(csvDocumentFile).uri,
            "w"
        )
        val dfWriter = PrintWriter(FileWriter(dfFileDescriptor?.fileDescriptor))

        var line = fReader.readLine()
        while (line != null) {
            dfWriter.write(line)
            dfWriter.println()
            line = fReader.readLine()
        }
        dfWriter.flush()
        dfWriter.close()
        dfFileDescriptor?.close()
        csvFile.delete()
    }

    fun createParentDirectory(selectedDirectoryUri: Uri): Uri {
        val selectedDirectory = DocumentFile.fromTreeUri(context, selectedDirectoryUri)!!
        val parentDirectory = selectedDirectory.createDirectory(PARENT_DIRECTORY_NAME)!!
        return parentDirectory.uri
    }

    private fun createThisExportDirectory(parentDirectoryUri: Uri): DocumentFile {
        val parentDirectory = DocumentFile.fromTreeUri(context, parentDirectoryUri)!!
        val sdf = SimpleDateFormat("MMMM_dd_yyyy__hh_mm_aa", Locale.getDefault())
        val formattedDate = sdf.format(Date())
        val thisExportDirectory = parentDirectory.createDirectory(formattedDate)!!
        return thisExportDirectory
    }
}