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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

        val categoryResult = importCsvToDatabaseEntity(csvDocumentFiles[0])
        updatePortationStatus(categoryResult.first)
        if (categoryResult.first is Error) return null

        val itemListResult = importCsvToDatabaseEntity(csvDocumentFiles[1])
        updatePortationStatus(itemListResult.first)
        if (itemListResult.first is Error) return null

        val defaultItemResult = importCsvToDatabaseEntity(csvDocumentFiles[2])
        updatePortationStatus(defaultItemResult.first)
        if (defaultItemResult.first is Error) return null

        val itemResult = importCsvToDatabaseEntity(csvDocumentFiles[3])
        updatePortationStatus(itemResult.first)
        if (itemResult.first is Error) return null

        return CsvData(
            categoryData = categoryResult.second as List<Category>,
            itemListData = itemListResult.second as List<ItemList>,
            defaultItemData = defaultItemResult.second as List<DefaultItem>,
            itemData = itemResult.second as List<Item>,
        )
    }

    private fun importCsvToDatabaseEntity(
        csvFile: DocumentFile,
    ): Pair<PortationStatus, List<CsvInfo>> {
        val inputStream = context.contentResolver.openInputStream(csvFile.uri)
            ?: return Pair(Error.ImportCorruptFile(csvFile.name!!), emptyList())
        try {
            val content = csvReader().readAll(inputStream)
            if (content.size == 1) {
                return Pair(Progress.ImportEntitySuccess(csvFile.name!!), emptyList())
            }

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
            return Pair(Progress.ImportEntitySuccess(csvFile.name!!), entityData)
        } catch (e: Exception) {
            return Pair(Error.ImportInvalidData(csvFile.name!!), emptyList())
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
                    // TODO Delete successfully created files and directory if there is a failure.
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
        val csvDocumentFile =
            newExportDirectory.createFile("text/*", "${entity.csvName}$CSV_SUFFIX")
                ?: return Error.CreateFileFailed("${entity.csvName}$CSV_SUFFIX")
        val outputStream = context.contentResolver.openOutputStream(csvDocumentFile.uri)
            ?: return Error.ExportEntityFailed(entity.csvName)
        csvWriter().open(outputStream) {
            writeRow(entity.csvHeader)
            entityData.forEach { writeRow(it.csvRow) }
        }
        return Progress.ExportEntitySuccess(entity.csvName)
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