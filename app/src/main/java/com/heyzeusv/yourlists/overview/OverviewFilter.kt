package com.heyzeusv.yourlists.overview

import com.heyzeusv.yourlists.SettingsFilter
import com.heyzeusv.yourlists.overview.OverviewFilterNames.BY_COMPLETION
import com.heyzeusv.yourlists.overview.OverviewFilterNames.BY_NAME
import com.heyzeusv.yourlists.util.FilterOption
import com.heyzeusv.yourlists.util.proto.defaultSettingsFilter

data class OverviewFilter(
    val byCompletion: Boolean = false,
    val byCompletionOption: FilterOption = FilterOption.ASC,
    val byName: Boolean = false,
    val byNameOption: FilterOption = FilterOption.ASC,
) {
    companion object {
        fun settingsFilterToOverviewFilter(filterList: List<SettingsFilter>): OverviewFilter {
            val byCompletionFilter = filterList.find { it.name == BY_COMPLETION }
                ?: defaultSettingsFilter(BY_COMPLETION)
            val byNameFilter =
                filterList.find { it.name == BY_NAME } ?: defaultSettingsFilter(BY_NAME)
            return OverviewFilter(
                byCompletion = byCompletionFilter.isSelected,
                byCompletionOption = FilterOption.fromSettingsFilter(byCompletionFilter),
                byName = byNameFilter.isSelected,
                byNameOption = FilterOption.fromSettingsFilter(byNameFilter),
            )
        }
    }
}

object OverviewFilterNames {
    const val BY_COMPLETION = "byCompletion"
    const val BY_NAME = "byName"

    val names = listOf(BY_COMPLETION, BY_NAME)
}