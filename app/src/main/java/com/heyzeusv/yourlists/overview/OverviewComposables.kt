package com.heyzeusv.yourlists.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.ItemList
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.BottomSheet
import com.heyzeusv.yourlists.util.EmptyList
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.FilterAlertDialog
import com.heyzeusv.yourlists.util.InputAlertDialog
import com.heyzeusv.yourlists.util.ListInfo
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.SingleFilterSelection
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.navigateToItemList
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OverviewScreen(
    overviewVM: OverviewViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
) {
    val itemLists by overviewVM.itemLists.collectAsStateWithLifecycle()
    val nextItemListId by overviewVM.nextItemListId.collectAsStateWithLifecycle()
    val settings by overviewVM.settings.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(OverviewFilter()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showNewListDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val topAppBarTitle = sRes(OverviewDestination.title)

    BackHandler(enabled = showBottomSheet) {
        showBottomSheet = false
    }
    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = OverviewDestination,
                title = topAppBarTitle,
                onActionRightPressed = { showFilterDialog = true },
            )
        )
    }
    LaunchedEffect(key1 = itemLists, key2 = showBottomSheet) {
        val isFabDisplayed = when {
            itemLists.isEmpty() || showBottomSheet -> false
            else -> true
        }
        fabSetup(
            FabState(
                isFabDisplayed = isFabDisplayed,
                fabAction = { showNewListDialog = true },
            )
        )
    }
    LaunchedEffect(key1 = settings) {
        filter = OverviewFilter.settingsFilterToOverviewFilter(settings.overviewFilterList)
    }
    OverviewScreen(
        listState = listState,
        itemLists = itemLists,
        itemListOnClick = { itemList ->
            itemList.itemList.let { navController.navigateToItemList(it.itemListId, it.name) }
        },
        emptyButtonOnClick = { showNewListDialog = true },
        showBottomSheet = showBottomSheet,
        updateShowBottomSheet = { showBottomSheet = it },
        optionRenameOnClick = overviewVM::renameItemList,
        optionCopyOnClick = overviewVM::copyItemList,
        optionDeleteOnClick = overviewVM::deleteItemList,
    )
    InputAlertDialog(
        display = showNewListDialog,
        onDismissRequest = { showNewListDialog = false },
        title = sRes(R.string.os_ad_new_title),
        maxLength = iRes(R.integer.name_max_length),
        onConfirm = { input ->
            overviewVM.insertItemList(input)
            navController.navigateToItemList(nextItemListId, input)
            showNewListDialog = false
        },
        onDismiss = { showNewListDialog = false }
    )
    FilterAlertDialog(
        display = showFilterDialog,
        title = sRes(R.string.fad_title),
        onConfirm = {
            showFilterDialog = false
            overviewVM.updateFilter(filter)
            scope.launch {
                delay(300)
                listState.animateScrollToItem(0)
            }
        },
        onDismiss = {
            showFilterDialog = false
            filter = OverviewFilter.settingsFilterToOverviewFilter(settings.overviewFilterList)
        },
    ) {
        OverviewFilter(
            filter = filter,
            updateFilter = { filter = it },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    listState: LazyListState = rememberLazyListState(),
    itemLists: List<ItemListWithItems>,
    itemListOnClick: (ItemListWithItems) -> Unit,
    emptyButtonOnClick: () -> Unit,
    showBottomSheet: Boolean,
    updateShowBottomSheet: (Boolean) -> Unit,
    optionRenameOnClick: (ItemList, String) -> Unit,
    optionCopyOnClick: (ItemListWithItems) -> Unit,
    optionDeleteOnClick: (ItemList) -> Unit,
) {
    var selectedItemList by remember { mutableStateOf(ItemListWithItems()) }
    var showRenameAlertDialog by remember { mutableStateOf<ItemList?>(null) }

    if (itemLists.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(all = dRes(R.dimen.os_lists_padding_all))
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = dRes(R.dimen.fab_padding_bottom)),
            verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.os_lists_spacedBy)),
        ) {
            items(
                items = itemLists.reversed(),
                key = { it.itemList.itemListId }
            ) {
                ListInfo(
                    modifier = Modifier.animateItemPlacement(),
                    itemList = it,
                    itemListOnClick = itemListOnClick,
                    displayOptions = true,
                    optionOnClick = { selected ->
                        selectedItemList = selected
                        updateShowBottomSheet(true)
                    },
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
    BottomSheet(
        isVisible = showBottomSheet,
        updateIsVisible = { updateShowBottomSheet(it) },
    ) {
        OverviewBottomSheetContent(
            itemList = selectedItemList,
            renameOnClick = {
                showRenameAlertDialog = selectedItemList.itemList
                updateShowBottomSheet(false)
            },
            copyOnClick = {
                optionCopyOnClick(selectedItemList)
                updateShowBottomSheet(false)
            },
            deleteOnClick = {
                optionDeleteOnClick(selectedItemList.itemList)
                updateShowBottomSheet(false)
            },
        )
    }
    InputAlertDialog(
        display = showRenameAlertDialog != null,
        onDismissRequest = { showRenameAlertDialog = null },
        title = sRes(R.string.os_ad_rename_title),
        maxLength = iRes(R.integer.name_max_length),
        onConfirm = { input ->
            optionRenameOnClick(showRenameAlertDialog!!, input)
            showRenameAlertDialog = null
        },
        onDismiss = { showRenameAlertDialog = null }
    )
}

@Composable
fun OverviewFilter(
    filter: OverviewFilter,
    updateFilter: (OverviewFilter) -> Unit,
) {
    Column {
        SingleFilterSelection(
            name = sRes(R.string.os_filter_byCompletion),
            isSelected = filter.byCompletion,
            updateIsSelected = { updateFilter(filter.copy(byCompletion = !filter.byCompletion)) },
            filterOption = filter.byCompletionOption,
            updateFilterOption = { updateFilter(filter.copy(byCompletionOption = it)) },
        )
        SingleFilterSelection(
            name = sRes(R.string.os_filter_byName),
            isSelected = filter.byName,
            updateIsSelected = { updateFilter(filter.copy(byName = !filter.byName)) },
            filterOption = filter.byNameOption,
            updateFilterOption = { updateFilter(filter.copy(byNameOption = it)) },
        )
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
            .padding(bottom = dRes(R.dimen.osbs_padding_bottom))
            .padding(all = dRes(R.dimen.bs_padding_all))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_vertical_spacedBy)),
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
        horizontalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_horizontal_spacedBy))
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
                itemLists = itemLists,
                itemListOnClick = { },
                emptyButtonOnClick = { },
                showBottomSheet = false,
                updateShowBottomSheet = { },
                optionRenameOnClick = { _, _ -> },
                optionCopyOnClick = { },
                optionDeleteOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun OverviewFilterPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                OverviewFilter(
                    filter = OverviewFilter(),
                    updateFilter = { },
                )
            }
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
                showBottomSheet = false,
                updateShowBottomSheet = { },
                optionRenameOnClick = { _, _ -> },
                optionCopyOnClick = { },
                optionDeleteOnClick = { },
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