package com.heyzeusv.yourlists.list

import com.heyzeusv.yourlists.util.FilterOption

data class ListFilter(
    val byIsChecked: FilterOption,
    val byName: FilterOption,
    val byCategory: FilterOption,
)