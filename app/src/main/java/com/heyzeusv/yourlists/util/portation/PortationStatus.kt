package com.heyzeusv.yourlists.util.portation

import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R

sealed class PortationStatus(@StringRes open val message: Int) {
    sealed class Error(@StringRes override val message: Int) : PortationStatus(message) {
        data object CreateDirectoryFailed : Error(R.string.p_error_create_directory_failed)
        data class CreateFileFailed(val file: String) : Error(R.string.p_error_create_file_failed)
        data class ExportEntityFailed(val file: String) : Error(R.string.p_error_export_table_failed)
        data object ExportMissingDirectory : Error(R.string.p_error_export_missing_directory)
        data class ImportCorruptFile(val file: String) : Error(R.string.p_error_import_corrupt_file)
        data class ImportInvalidData(val file: String) : Error(R.string.p_error_import_invalid_data)
        data object ImportMissingDirectory : Error(R.string.p_error_import_missing_directory)
        data class ImportMissingFile(val file: String) : Error(R.string.p_error_import_missing_file)
    }
    sealed class Progress(@StringRes override val message: Int) : PortationStatus(message) {
        data class ExportEntitySuccess(val file: String) : Progress(R.string.p_progress_export_entity_success)
        data object ExportStarted : Progress(R.string.p_progress_export_started)
        data object ExportSuccess : Progress(R.string.p_progress_export_success)
        data class ImportEntitySuccess(val file: String) : Progress(R.string.p_progress_import_entity_success)
        data object ImportStarted : Progress(R.string.p_progress_import_started)
        data object ImportSuccess : Progress(R.string.p_progress_import_success)
        data object ImportUpdateDatabase : Progress(R.string.p_progress_import_update_database)
    }
    data object Standby : PortationStatus(R.string.blank_string)
}