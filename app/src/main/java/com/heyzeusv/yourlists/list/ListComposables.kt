package com.heyzeusv.yourlists.list

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.EmptyList
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.InputAlertDialog
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.navigateSingleTopTo
import com.heyzeusv.yourlists.util.sRes

@Composable
fun ListScreen(
    listVM: ListViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
    topAppBarTitle: String?,
) {
    val itemList by listVM.itemList.collectAsStateWithLifecycle()
    var isNewList by remember { mutableStateOf(topAppBarTitle == null) }
    val newListTitle = sRes(ListDestination.title)

    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = ListDestination,
                title = topAppBarTitle ?: newListTitle,
                onNavPressed = { navController.navigateUp() },
            )
        )
    }
    LaunchedEffect(key1 = itemList.items) {
        // TODO Check this again once Items are able to be added
        fabSetup(
            FabState(
                isFabDisplayed = itemList.items.isNotEmpty(),
                fabAction = { navController.navigateSingleTopTo(AddDestination.route) },
            )
        )
    }
    InputAlertDialog(
        display = isNewList,
        onDismissRequest = { },
        title = sRes(R.string.ls_ad_title),
        maxLength = iRes(R.integer.title_max_length),
        onConfirm = { input ->
            listVM.insertItemList(input)
            topAppBarSetup(
                TopAppBarState(
                    destination = ListDestination,
                    title = input,
                    onNavPressed = { navController.navigateUp() },
                )
            )
            isNewList = false
        },
        onDismiss = {
            navController.navigateUp()
            isNewList = false
        }
    )
    ListScreen(
        itemList = itemList,
        emptyButtonOnClick = { navController.navigateSingleTopTo(AddDestination.route) },
    )
}

@Composable
fun ListScreen(
    itemList: ItemListWithItems,
    emptyButtonOnClick: () -> Unit,
) {
    val listState = rememberLazyListState()

    if (itemList.items.isNotEmpty()) {
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
    } else {
        EmptyList(
            message = sRes(R.string.ls_empty),
            buttonOnClick = emptyButtonOnClick,
            buttonIcon = ListDestination.fabIcon,
            buttonText = sRes(ListDestination.fabText)
        )
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
        Preview {
            ListScreen(
                itemList = halfCheckedItemList,
                emptyButtonOnClick = { }
            )
        }
    }
}

@Preview
@Composable
fun ListScreenEmptyPreview() {
    PreviewUtil.run {
        Preview {
            ListScreen(
                itemList = emptyItemList,
                emptyButtonOnClick = { }
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