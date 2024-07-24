package com.heyzeusv.yourlists.list

import com.heyzeusv.yourlists.util.FilterOption

data class ListFilter(
    val byIsChecked: Boolean = false,
    val byIsCheckedOption: FilterOption = FilterOption.ASC,
    val byName: Boolean = false,
    val byNameOption: FilterOption = FilterOption.ASC,
    val byCategory: Boolean = false,
    val byCategoryOption: FilterOption = FilterOption.ASC,
)