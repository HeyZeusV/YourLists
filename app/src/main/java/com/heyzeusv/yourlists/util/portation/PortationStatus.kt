package com.heyzeusv.yourlists.util.portation

import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R

sealed class PortationStatus(@StringRes open val message: Int) {
    sealed class Error(@StringRes override val message: Int) : PortationStatus(message) {
        data object CreateDirectoryFailed : Error(R.string.p_error_create_directory_failed)
        data class CreateFileFailed(val file: String) : Error(R.string.p_error_create_file_failed)
        data class ExportEntityFailed(val file: String) : Error(R.string.p_error_export_table_failed)
        data object MissingDirectory : Error(R.string.p_error_missing_directory)
    }
    sealed class Progress(@StringRes override val message: Int) : PortationStatus(message) {
        data class ExportEntitySuccess(val file: String) : Progress(R.string.p_progress_export_entity_success)
        data object ExportStarted : Progress(R.string.p_progress_export_started)
        data object ExportSuccess : Progress(R.string.p_progress_export_success)
    }
    data object Standby : PortationStatus(R.string.blank_string)
}