package com.heyzeusv.yourlists.util.portation

import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R

sealed class PortationStatus(@StringRes open val message: Int, open val file: String = "") {
    sealed class Error(
        @StringRes override val message: Int,
        override val file: String = "",
    ) : PortationStatus(message, file) {
        data object CreateDirectoryFailed : Error(R.string.p_error_create_directory_failed)
        data class CreateFileFailed(override val file: String) : Error(R.string.p_error_create_file_failed, file)
        data class ExportEntityFailed(override val file: String) : Error(R.string.p_error_export_table_failed, file)
        data object ExportMissingDirectory : Error(R.string.p_error_export_missing_directory)
        data class ImportCorruptFile(override val file: String) : Error(R.string.p_error_import_corrupt_file, file)
        data class ImportInvalidData(override val file: String) : Error(R.string.p_error_import_invalid_data, file)
        data object ImportMissingDirectory : Error(R.string.p_error_import_missing_directory)
        data class ImportMissingFile(override val file: String) : Error(R.string.p_error_import_missing_file, file)
    }
    sealed class Progress(
        @StringRes override val message: Int,
        override val file: String = "",
    ) : PortationStatus(message, file) {
        data class ExportEntitySuccess(override val file: String) : Progress(R.string.p_progress_export_entity_success, file)
        data object ExportStarted : Progress(R.string.p_progress_export_started)
        data object ExportSuccess : Progress(R.string.p_progress_export_success)
        data class ImportEntitySuccess(override val file: String) : Progress(R.string.p_progress_import_entity_success, file)
        data object ImportStarted : Progress(R.string.p_progress_import_started)
        data object ImportSuccess : Progress(R.string.p_progress_import_success)
        data object ImportUpdateDatabase : Progress(R.string.p_progress_import_update_database)
    }
    data object Standby : PortationStatus(R.string.blank_string)
}