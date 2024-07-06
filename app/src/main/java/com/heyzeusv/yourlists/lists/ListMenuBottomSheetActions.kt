package com.heyzeusv.yourlists.lists

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.heyzeusv.yourlists.R

enum class ListMenuBottomSheetActions(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int,
    @StringRes val iconCdescId: Int,
) {
    RENAME(
        nameId = R.string.lmbs_rename,
        iconId = R.drawable.icon_rename,
        iconCdescId = R.string.lmbs_cdesc_rename,
    ),
    COPY(
        nameId = R.string.lmbs_copy,
        iconId = R.drawable.icon_copy,
        iconCdescId = R.string.lmbs_cdesc_copy,
    ),
    DELETE(
        nameId = R.string.lmbs_delete,
        iconId = R.drawable.icon_delete,
        iconCdescId = R.string.lmbs_cdesc_delete,
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