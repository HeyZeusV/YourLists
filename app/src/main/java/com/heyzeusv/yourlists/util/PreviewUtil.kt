package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.heyzeusv.yourlists.ui.theme.YourListsTheme

/**
 *  Helper object to create Compose Previews.
 */
object PreviewUtil {

    @Composable
    fun Preview(content: @Composable () -> Unit) {
        YourListsTheme {
            Box(modifier = Modifier.fillMaxWidth())  {
                content()
            }
        }
    }
}