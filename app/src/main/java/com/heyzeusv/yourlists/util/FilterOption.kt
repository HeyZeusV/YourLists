package com.heyzeusv.yourlists.util

object FilterValue {
    const val NONE = "NONE"
    const val ASC = "ASC"
    const val DESC = "DESC"
}

enum class FilterOption(val value: String) {
    NONE(FilterValue.NONE),
    ASC(FilterValue.ASC),
    DESC(FilterValue.DESC)
}