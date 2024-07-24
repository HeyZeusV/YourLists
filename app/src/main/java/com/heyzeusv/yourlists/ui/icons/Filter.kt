package com.heyzeusv.yourlists.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Filter: ImageVector
    get() {
        if (mFilter != null) {
            return mFilter!!
        }
        mFilter = materialIcon(name = "Filled.Filter") {
            materialPath {
                // M3,4c2.01,2.59 7,9 7,9v7h4v-7c0,0 4.98,-6.41 7,-9H3z
                moveTo(3f, 4f)
                curveToRelative(2.01f, 2.59f, 7f, 9f, 7f, 9f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-7f)
                curveToRelative(0f, 0f, 4.98f, -6.41f, 7f, -9f)
                horizontalLineTo(3f)
                close()
            }
        }
        return mFilter!!
    }

private var mFilter: ImageVector? = null