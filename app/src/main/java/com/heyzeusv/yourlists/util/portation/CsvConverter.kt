package com.heyzeusv.yourlists.util.portation

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.util.portation.PortationStatus.Error
import com.heyzeusv.yourlists.util.portation.PortationStatus.Progress
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
private const val CATEGORY_CSV = "Category.csv"
private const val ITEM_LIST_CSV = "ItemList.csv"
private const val DEFAULT_ITEM_CSV = "DefaultItem.csv"
private const val ITEM_CSV = "Item.csv"
private const val CSV_SUFFIX = ".csv"

class CsvConverter @Inject constructor(
    private val context: Context,
) {
    private val csvFileNames =
        listOf(CATEGORY_CSV, ITEM_LIST_CSV, DEFAULT_ITEM_CSV, ITEM_CSV)

    @Suppress("UNCHECKED_CAST")
    fun importCsvToDatabase(
        selectedDirectoryUri: Uri,
        updatePortationStatus: (PortationStatus) -> Unit,
    ): CsvData? {
        updatePortationStatus(Progress.ImportStarted)
        val selectedDirectory = DocumentFile.fromTreeUri(context, selectedDirectoryUri)!!
        if (!selectedDirectory.exists()) {
            updatePortationStatus(Error.ImportMissingDirectory)
            return null
        }
        val csvDocumentFiles = mutableListOf<DocumentFile>()
        csvFileNames.forEach {
            val csvDocumentFile = selectedDirectory.findFile(it)
            if (csvDocumentFile == null) {
                updatePortationStatus(Error.ImportMissingFile(it))
                return null
            } else {
                csvDocumentFiles.add(csvDocumentFile)
            }
        }
        val csvFiles = mutableListOf<File>()
        csvDocumentFiles.forEach {
            val csvFile = writeToFileFromDocumentFile(it)
            if (csvFile == null) {
                csvFiles.forEach { file -> file.delete() }
                updatePortationStatus(Error.ImportCorruptFile(it.name!!))
                return null
            }
            updatePortationStatus(Progress.ImportEntitySuccess(it.name!!.removeSuffix(CSV_SUFFIX)))
        }
        val categoryData = importCsvToDatabaseEntity(CATEGORY_CSV) as List<Category>?
        if (categoryData == null) {
            updatePortationStatus(Error.ImportInvalidData(CATEGORY_CSV))
            return null
        }
        val itemListData = importCsvToDatabaseEntity(ITEM_LIST_CSV) as List<ItemList>?
        if (itemListData == null) {
            updatePortationStatus(Error.ImportInvalidData(ITEM_LIST_CSV))
            return null
        }
        val defaultItemData = importCsvToDatabaseEntity(DEFAULT_ITEM_CSV) as List<DefaultItem>?
        if (defaultItemData == null) {
            updatePortationStatus(Error.ImportInvalidData(DEFAULT_ITEM_CSV))
            return null
        }
        val itemData = importCsvToDatabaseEntity(ITEM_CSV) as List<Item>?
        if (itemData == null) {
            updatePortationStatus(Error.ImportInvalidData(ITEM_CSV))
            return null
        }

        return CsvData(
            categoryData = categoryData,
            itemListData = itemListData,
            defaultItemData = defaultItemData,
            itemData = itemData,
        )
    }

    private fun importCsvToDatabaseEntity(csvFileName: String): List<CsvInfo>? {
        var csvFile: File? = null
        try {
            csvFile = File(context.filesDir, csvFileName)
            val content = csvReader().readAll(csvFile)
            if (content.size == 1) return emptyList()

            val header = content[0]
            val rows = content.drop(1)
            val entityData = mutableListOf<CsvInfo>()
            when (header) {
                Category().csvHeader -> {
                    rows.forEach {
                        val entry = Category(id = it[0].toLong(), name = it[1])
                        entityData.add(entry)
                    }
                }

                ItemList().csvHeader -> {
                    rows.forEach {
                        val entry = ItemList(itemListId = it[0].toLong(), name = it[1])
                        entityData.add(entry)
                    }
                }

                DefaultItem().csvHeader -> {
                    rows.forEach {
                        val entry = DefaultItem(
                            itemId = it[0].toLong(),
                            name = it[1],
                            category = it[2],
                            quantity = it[3].toDouble(),
                            unit = it[4],
                            memo = it[5],
                        )
                        entityData.add(entry)
                    }
                }

                Item().csvHeader -> {
                    rows.forEach {
                        val entry = Item(
                            itemId = it[0].toLong(),
                            name = it[1],
                            isChecked = it[2].toBoolean(),
                            category = it[3],
                            quantity = it[4].toDouble(),
                            unit = it[5],
                            memo = it[6],
                            parentItemListId = it[7].toLong(),
                            originItemListId = it[8].toLongOrNull()
                        )
                        entityData.add(entry)
                    }
                }
            }
            csvFile.delete()
            return entityData
        } catch (e: Exception) {
            csvFile?.delete()
            return null
        }
    }

    private fun writeToFileFromDocumentFile(csvDocumentFile: DocumentFile): File? {
        var csvFile: File? = null
        try {
            csvFile = File(context.filesDir, csvDocumentFile.name!!).apply {
                delete()
                createNewFile()
            }
            val dfInputStream = context.contentResolver.openInputStream(csvDocumentFile.uri)
            val dfReader = BufferedReader(InputStreamReader(dfInputStream))

            val fWriter = PrintWriter(FileWriter(csvFile))

            var line = dfReader.readLine()
            while (line != null) {
                fWriter.write(line)
                fWriter.println()
                line = dfReader.readLine()
            }

            dfReader.close()
            fWriter.flush()
            fWriter.close()
            return csvFile
        } catch (e: Exception) {
            csvFile?.delete()
            return null
        }
    }

    fun exportDatabaseToCsv(
        parentDirectoryUri: Uri,
        csvData: CsvData,
        updatePortationStatus: (PortationStatus) -> Unit,
    ) {
        updatePortationStatus(Progress.ExportStarted)
        val parentDirectory = DocumentFile.fromTreeUri(context, parentDirectoryUri)!!
        if (!parentDirectory.exists()) {
            updatePortationStatus(Error.ExportMissingDirectory)
            return
        } else {
            val newExportDirectory = createNewExportDirectory(parentDirectory)
            if (newExportDirectory == null) {
                updatePortationStatus(Error.CreateDirectoryFailed)
                return
            } else {
                csvData.entityDataPair.forEach {
                    val portationStatus =
                        exportDatabaseEntityToCsv(newExportDirectory, it.first, it.second)
                    updatePortationStatus(portationStatus)
                    if (portationStatus is Error) return
                }
                updatePortationStatus(Progress.ExportSuccess)
            }
        }
    }

    private fun exportDatabaseEntityToCsv(
        newExportDirectory: DocumentFile,
        entity: CsvInfo,
        entityData: List<CsvInfo>,
    ): PortationStatus {
        val csvFile = createAndWriteEntityDataToFile(entity, entityData)
        val csvDocumentFile =
            newExportDirectory.createFile("text/*", "${entity.csvName}$CSV_SUFFIX")
                ?: return Error.CreateFileFailed("${entity.csvName}$CSV_SUFFIX")
        val portationStatus = writeToDocumentFileFromFile(csvFile, csvDocumentFile)
        return portationStatus
    }

    private fun createAndWriteEntityDataToFile(
        entity: CsvInfo,
        entityData: List<CsvInfo>,
    ): File {
        val csvFile = File(context.filesDir, "${entity.csvName}$CSV_SUFFIX").apply {
            delete()
            createNewFile()
        }
        csvWriter().open(csvFile) {
            writeRow(entity.csvHeader)
            entityData.forEach { writeRow(it.csvRow) }
        }
        return csvFile
    }

    private fun writeToDocumentFileFromFile(
        csvFile: File,
        csvDocumentFile: DocumentFile
    ): PortationStatus {
        try {
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
        } catch (e: Exception) {
            return Error.ExportEntityFailed(csvFile.name.removeSuffix(CSV_SUFFIX))
        }
        return Progress.ExportEntitySuccess(csvFile.name.removeSuffix(CSV_SUFFIX))
    }

    private fun createNewExportDirectory(parentDirectory: DocumentFile): DocumentFile? {
        val sdf = SimpleDateFormat("MMMM_dd_yyyy__hh_mm_aa", Locale.getDefault())
        val formattedDate = sdf.format(Date())
        val newExportDirectory = parentDirectory.createDirectory(formattedDate)
        return newExportDirectory
    }

    fun findOrCreateParentDirectory(selectedDirectoryUri: Uri): Uri? {
        try {
            val selectedDirectory = DocumentFile.fromTreeUri(context, selectedDirectoryUri)!!
            var parentDirectory = selectedDirectory.findFile(PARENT_DIRECTORY_NAME)
            if (parentDirectory == null) {
                parentDirectory = selectedDirectory.createDirectory(PARENT_DIRECTORY_NAME)!!
            }
            return parentDirectory.uri
        } catch (e: Exception) {
            return null
        }
    }
}