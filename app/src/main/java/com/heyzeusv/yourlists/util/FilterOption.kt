package com.heyzeusv.yourlists.util

import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.SettingsFilterOption

object FilterValue {
    const val ASC = "ASC"
    const val DESC = "DESC"
}

enum class FilterOption(
    @StringRes val nameId: Int,
    val value: String,
    val sFilterOption: SettingsFilterOption,
) {
    ASC(
        nameId = R.string.fad_asc,
        value = FilterValue.ASC,
        sFilterOption = SettingsFilterOption.ASC,
    ),
    DESC(
        nameId = R.string.fad_desc,
        value = FilterValue.DESC,
        sFilterOption = SettingsFilterOption.DESC,
    )
}