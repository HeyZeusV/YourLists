package com.heyzeusv.yourlists.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.EmptyList
import com.heyzeusv.yourlists.util.InputAlertDialog
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.navigateToItemListWithId
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes

@Composable
fun OverviewScreen(
    overviewVM: OverviewViewModel,
    navController: NavHostController,
    siSetUp: (TopAppBarState) -> Unit,
) {
    BackHandler {
        navController.navigateUp()
    }
    val itemLists by overviewVM.itemLists.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = itemLists.size) {
        siSetUp(
            TopAppBarState(
                destination = OverviewDestination,
//                isFabDisplayed = itemLists.isNotEmpty(),
//                fabAction = { navController.navigateToItemListWithId(-1) },
            )
        )
    }
    OverviewScreen(
        itemLists = itemLists,
        itemListOnClick = { navController.navigateToItemListWithId(it) },
        emptyButtonOnClick = { navController.navigateToItemListWithId(-1) },
        optionRenameOnClick = overviewVM::renameItemList,
        optionCopyOnClick = overviewVM::copyItemList,
        optionDeleteOnClick = overviewVM::deleteItemList,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    itemLists: List<ItemListWithItems>,
    itemListOnClick: (Long) -> Unit,
    emptyButtonOnClick: () -> Unit,
    optionRenameOnClick: (ItemList, String) -> Unit,
    optionCopyOnClick: (ItemListWithItems) -> Unit,
    optionDeleteOnClick: (ItemList) -> Unit,
) {
    val listState = rememberLazyListState()
    var showBottomSheet by remember { mutableStateOf<ItemListWithItems?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showRenameAlertDialog by remember { mutableStateOf<ItemList?>(null) }

    if (itemLists.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(all = dRes(R.dimen.os_lists_padding_all))
                .fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.os_lists_spacedBy)),
        ) {
            items(itemLists.reversed()) {
                ListInfo(
                    itemList = it,
                    itemListOnClick = itemListOnClick,
                    optionOnClick = { selected -> showBottomSheet = selected },
                )
            }
        }
    } else {
        EmptyList(
            message = sRes(R.string.os_empty),
            buttonOnClick = emptyButtonOnClick,
            buttonIcon = OverviewDestination.fabIcon,
            buttonText = sRes(OverviewDestination.fabText)
        )
    }
    if (showBottomSheet != null) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = null },
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            dragHandle = { },
        ) {
            OverviewBottomSheetContent(
                itemList = showBottomSheet!!,
                renameOnClick = {
                    showRenameAlertDialog = showBottomSheet!!.itemList
                    showBottomSheet = null
                },
                copyOnClick = {
                    optionCopyOnClick(showBottomSheet!!)
                    showBottomSheet = null
                },
                deleteOnClick = {
                    optionDeleteOnClick(showBottomSheet!!.itemList)
                    showBottomSheet = null
                },
            )
        }
    }
    InputAlertDialog(
        display = showRenameAlertDialog != null,
        onDismissRequest = { showRenameAlertDialog = null },
        title = sRes(R.string.os_ad_rename_title),
        maxLength = iRes(R.integer.title_max_length),
        onConfirm = { input ->
            optionRenameOnClick(showRenameAlertDialog!!, input)
            showRenameAlertDialog = null
        },
        onDismiss = { showRenameAlertDialog = null }
    )
}

@Composable
fun ListInfo(
    itemList: ItemListWithItems,
    itemListOnClick: (Long) -> Unit,
    optionOnClick: (ItemListWithItems) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { itemListOnClick(itemList.itemList.itemListId) },
        shape = RoundedCornerShape(dRes(R.dimen.card_radius)),
    ) {
        Column(modifier = Modifier.padding(all = dRes(R.dimen.osli_padding_all))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemList.itemList.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium
                )
                Icon(
                    painter = pRes(R.drawable.icon_options),
                    contentDescription = sRes(R.string.button_cdesc_options),
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(top = dRes(R.dimen.osli_options_padding_top))
                        .clickable { optionOnClick(itemList) },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.osli_progress_spacedBy)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { itemList.progress.first },
                    modifier = Modifier
                        .height(dRes(R.dimen.osli_progress_height))
                        .weight(1f),
                    trackColor = MaterialTheme.colorScheme.background,
                    strokeCap = StrokeCap.Round
                )
                Text(text = itemList.progress.second)
            }
        }
    }
}

@Composable
fun OverviewBottomSheetContent(
    itemList: ItemListWithItems,
    renameOnClick: () -> Unit,
    copyOnClick: () -> Unit,
    deleteOnClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.osbs_padding_all))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.osbs_vertical_spacedBy)),
    ) {
        Text(
            text = sRes(R.string.osbs_manage, itemList.itemList.name),
            style = MaterialTheme.typography.headlineMedium
        )
        OverviewBottomSheetAction(
            action = OverviewBottomSheetActions.RENAME,
            actionOnClick = renameOnClick,
        )
        OverviewBottomSheetAction(
            action = OverviewBottomSheetActions.COPY,
            actionOnClick = copyOnClick,
        )
        OverviewBottomSheetAction(
            action = OverviewBottomSheetActions.DELETE,
            actionOnClick = deleteOnClick,
        )
    }
}

@Composable
fun OverviewBottomSheetAction(
    action: OverviewBottomSheetActions,
    actionOnClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { actionOnClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.osbs_horizontal_spacedBy))
    ) {
        Icon(
            painter = pRes(action.iconId),
            contentDescription = sRes(action.iconCdescId),
            modifier = Modifier.height(action.iconSize),
            tint = action.color
        )
        Text(
            text = sRes(action.nameId),
            color = action.color,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview
@Composable
private fun OverviewScreenPreview() {
    PreviewUtil.run {
        Preview {
            OverviewScreen(
                itemLists = List(15) { halfCheckedItemList },
                itemListOnClick = { },
                emptyButtonOnClick = { },
                optionRenameOnClick = { _, _ -> },
                optionCopyOnClick = { },
                optionDeleteOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun OverviewScreenEmptyPreview() {
    PreviewUtil.run {
        Preview {
            OverviewScreen(
                itemLists = emptyList(),
                itemListOnClick = { },
                emptyButtonOnClick = { },
                optionRenameOnClick = { _, _ -> },
                optionCopyOnClick = { },
                optionDeleteOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun ListInfoPreview() {
    PreviewUtil.run {
        Preview {
            ListInfo(
                itemList = halfCheckedItemList,
                itemListOnClick = { },
                optionOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun OverviewBottomSheetPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                OverviewBottomSheetContent(
                    itemList = halfCheckedItemList,
                    renameOnClick = { },
                    copyOnClick = { },
                    deleteOnClick = { },
                )
            }
        }
    }
}

@Preview
@Composable
private fun OverviewBottomSheetActionPreview() {
    PreviewUtil.run {
        Preview {
            Surface(modifier = Modifier.fillMaxWidth()) {
                OverviewBottomSheetAction(
                    action = OverviewBottomSheetActions.DELETE,
                    actionOnClick = { },
                )
            }
        }
    }
}

