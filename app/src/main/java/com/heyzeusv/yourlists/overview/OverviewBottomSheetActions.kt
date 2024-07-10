package com.heyzeusv.yourlists.overview

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.yourlists.R

enum class OverviewBottomSheetActions(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int,
    @StringRes val iconCdescId: Int,
    val iconSize: Dp,
) {
    RENAME(
        nameId = R.string.osbs_rename,
        iconId = R.drawable.icon_rename,
        iconCdescId = R.string.osbs_cdesc_rename,
        iconSize = 28.dp,
    ),
    COPY(
        nameId = R.string.osbs_copy,
        iconId = R.drawable.icon_copy,
        iconCdescId = R.string.osbs_cdesc_copy,
        iconSize = 20.dp,
    ),
    DELETE(
        nameId = R.string.osbs_delete,
        iconId = R.drawable.icon_delete,
        iconCdescId = R.string.osbs_cdesc_delete,
        iconSize = 28.dp,
    )

    ;

    val color: Color
        @Composable
        @ReadOnlyComposable
        get() = when(this) {
            DELETE -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
}