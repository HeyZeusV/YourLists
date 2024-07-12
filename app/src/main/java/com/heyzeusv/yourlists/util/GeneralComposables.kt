package com.heyzeusv.yourlists.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.BaseItem
import com.heyzeusv.yourlists.database.models.Item

@Composable
fun EmptyList(
    message: String,
    buttonOnClick: () -> Unit,
    buttonIcon: ImageVector,
    buttonText: String,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = dRes(R.dimen.el_padding_horizontal))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(bottom = dRes(R.dimen.el_text_padding_bottom)),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = buttonOnClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = buttonText,
                modifier = Modifier.size(dRes(R.dimen.el_icon_size))
            )
            Text(
                text = buttonText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInfo(
    item: BaseItem
) {
    Surface {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = dRes(R.dimen.lsif_padding_horizontal),
                    vertical = dRes(R.dimen.lsif_padding_vertical)
                )
                .heightIn(min = dRes(R.dimen.lsif_height_min))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.lsif_spacedBy)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item is Item) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { },
                    )
                }
            }
            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${item.quantity} ${item.unit}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun EmptyListPreview() {
    PreviewUtil.run {
        Preview {
            EmptyList(
                message = "Blah Blah Blah",
                buttonOnClick = { },
                buttonIcon = Icons.Filled.Add,
                buttonText = "More Blah Blah",
            )
        }
    }
}

@Preview
@Composable
private fun ItemInfoPreview() {
    PreviewUtil.run {
        Preview {
            Column {
                ItemInfo(item = itemChecked)
                ItemInfo(item = itemUnchecked)
            }
        }
    }
}

@Preview
@Composable
private fun DefaultItemInfoPreview() {
    PreviewUtil.run {
        Preview {
            Column {
                ItemInfo(item = defaultItem)
                ItemInfo(item = defaultItem)
            }
        }
    }
}