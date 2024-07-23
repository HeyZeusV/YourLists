package com.heyzeusv.yourlists.list

import com.heyzeusv.yourlists.util.FilterOptions

data class ListFilters(
    val byIsChecked: FilterOptions,
    val byName: FilterOptions,
    val byCategory: FilterOptions,
)