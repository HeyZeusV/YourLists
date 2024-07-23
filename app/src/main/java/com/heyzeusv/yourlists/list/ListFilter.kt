package com.heyzeusv.yourlists.list

import com.heyzeusv.yourlists.util.FilterOption

data class ListFilter(
    val byIsChecked: Boolean,
    val byIsCheckedOption: FilterOption,
    val byName: Boolean,
    val byNameOption: FilterOption,
    val byCategory: Boolean,
    val byCategoryOption: FilterOption,
)