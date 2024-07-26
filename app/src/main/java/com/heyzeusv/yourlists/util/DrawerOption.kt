package com.heyzeusv.yourlists.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heyzeusv.yourlists.R

enum class DrawerOption(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int,
) {
    IMPORT(
        nameId = R.string.d_import,
        iconId = R.drawable.icon_import,
    ),
    EXPORT(
        nameId = R.string.d_export,
        iconId = R.drawable.icon_export,
    )
}