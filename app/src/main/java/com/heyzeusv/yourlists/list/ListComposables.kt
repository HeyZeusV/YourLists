package com.heyzeusv.yourlists.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.ScaffoldActions
import com.heyzeusv.yourlists.util.dRes

@Composable
fun ListScreen(
    listVM: ListViewModel,
    saSetUp: (ScaffoldActions) -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler {
        onBackPressed()
    }
    saSetUp(
        ScaffoldActions(
            destination = ListDestination,
            topBarNavPressed = onBackPressed
        )
    )

    val itemList by listVM.itemList.collectAsStateWithLifecycle()

    ListScreen(itemList = itemList)
}

@Composable
fun ListScreen(
    itemList: ItemListWithItems
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .padding(all = dRes(R.dimen.ls_list_padding_all))
            .fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.ls_list_spacedBy))
    ) {
        items(itemList.items) {
            ItemInfo(item = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInfo(
    item: Item
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
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { },
                )
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
fun ListScreenPreview() {
    PreviewUtil.run {
        ListScreen(itemList = halfCheckedItemList)
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