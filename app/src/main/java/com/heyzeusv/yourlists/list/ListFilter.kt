package com.heyzeusv.yourlists.list

import com.heyzeusv.yourlists.SettingsFilter
import com.heyzeusv.yourlists.list.ListFilterNames.BY_CATEGORY
import com.heyzeusv.yourlists.list.ListFilterNames.BY_IS_CHECKED
import com.heyzeusv.yourlists.list.ListFilterNames.BY_NAME
import com.heyzeusv.yourlists.util.FilterOption

data class ListFilter(
    val byIsChecked: Boolean = false,
    val byIsCheckedOption: FilterOption = FilterOption.ASC,
    val byName: Boolean = false,
    val byNameOption: FilterOption = FilterOption.ASC,
    val byCategory: Boolean = false,
    val byCategoryOption: FilterOption = FilterOption.ASC,
) {

    companion object {
        fun settingsFilterToListFilter(filterList: List<SettingsFilter>): ListFilter {
            val byIsCheckedFilter = filterList.find { it.name == BY_IS_CHECKED }
                ?: SettingsFilter.getDefaultInstance()
            val byNameFilter =
                filterList.find { it.name == BY_NAME } ?: SettingsFilter.getDefaultInstance()
            val byCategoryFilter = filterList.find { it.name == BY_CATEGORY }
                ?: SettingsFilter.getDefaultInstance()
            return ListFilter(
                byIsChecked = byIsCheckedFilter.isSelected,
                byIsCheckedOption = FilterOption.entries[byIsCheckedFilter.filterOptionValue],
                byName = byNameFilter.isSelected,
                byNameOption = FilterOption.entries[byNameFilter.filterOptionValue],
                byCategory = byCategoryFilter.isSelected,
                byCategoryOption = FilterOption.entries[byCategoryFilter.filterOptionValue],
            )
        }
    }
}

object ListFilterNames {
    const val BY_IS_CHECKED = "byIsChecked"
    const val BY_NAME = "byName"
    const val BY_CATEGORY = "byCategory"

    val names = listOf(BY_IS_CHECKED, BY_NAME, BY_CATEGORY)
}