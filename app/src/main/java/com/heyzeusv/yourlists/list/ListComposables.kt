package com.heyzeusv.yourlists.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.Item
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.BottomSheet
import com.heyzeusv.yourlists.util.EditItemBottomSheetContent
import com.heyzeusv.yourlists.util.EmptyList
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.FilterAlertDialog
import com.heyzeusv.yourlists.util.ItemInfo
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.SingleFilterSelection
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.navigateToAdd
import com.heyzeusv.yourlists.util.sRes

@Composable
fun ListScreen(
    listVM: ListViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
    topAppBarTitle: String,
) {
    val itemList by listVM.itemList.collectAsStateWithLifecycle()
    val items by listVM.items.collectAsStateWithLifecycle()
    val categories by listVM.categories.collectAsStateWithLifecycle()
    val settings by listVM.settings.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(ListFilter()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = showBottomSheet) {
        showBottomSheet = false
    }
    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = ListDestination,
                title = topAppBarTitle,
                onNavPressed = { navController.navigateUp() },
                onActionRightPressed = { showFilterDialog = true },
            )
        )
    }
    LaunchedEffect(key1 = items, key2 = showBottomSheet) {
        val isFabDisplayed = when {
            items.isEmpty() || showBottomSheet -> false
            else -> true
        }
        fabSetup(
            FabState(
                isFabDisplayed = isFabDisplayed,
                fabAction = { navController.navigateToAdd(itemList.itemListId) },
            )
        )
    }
    LaunchedEffect(key1 = settings) {
        filter = ListFilter.settingsFilterToListFilter(settings.listFiltersList)
    }
    ListScreen(
        itemList = ItemListWithItems(itemList, items),
        categories = categories,
        emptyButtonOnClick = { navController.navigateToAdd(itemList.itemListId) },
        checkboxOnClick = listVM::updateItemIsChecked,
        showBottomSheet = showBottomSheet,
        updateShowBottomSheet = { showBottomSheet = it },
        updateOnClick = listVM::updateItem,
        deleteOnClick = listVM::deleteItem,
    )
    FilterAlertDialog(
        display = showFilterDialog,
        title = sRes(R.string.fad_title),
        onConfirm = {
            showFilterDialog = false
            listVM.updateFilter(filter)
        },
        onDismiss = { showFilterDialog = false },
    ) {
        ListFilters(
            filter = filter,
            updateFilter = { filter = it },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    itemList: ItemListWithItems,
    categories: List<Category>,
    emptyButtonOnClick: () -> Unit,
    checkboxOnClick: (Item, (Boolean) -> Unit) -> Unit,
    showBottomSheet: Boolean,
    updateShowBottomSheet: (Boolean) -> Unit,
    updateOnClick: (Item) -> Unit,
    deleteOnClick: (Item) -> Unit,
) {
    val listState = rememberLazyListState()
    var selectedItem by remember { mutableStateOf(Item()) }

    if (itemList.items.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(all = dRes(R.dimen.ls_list_padding_all))
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = dRes(R.dimen.fab_padding_bottom)),
            verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.ls_list_spacedBy))
        ) {
            items(
                items = itemList.items,
                key = { it.itemId },
            ) {
                ItemInfo(
                    modifier = Modifier.animateItemPlacement(),
                    item = it,
                    surfaceOnClick = {
                        selectedItem = it
                        updateShowBottomSheet(true)
                    },
                    checkboxOnClick = checkboxOnClick,
                )
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
    BottomSheet(
        isVisible = showBottomSheet,
        updateIsVisible = { updateShowBottomSheet(it) },
    ) {
        EditItemBottomSheetContent(
            closeBottomSheet = { updateShowBottomSheet(false) },
            selectedItem = selectedItem,
            categories = categories,
            primaryLabel = sRes(R.string.lsbs_update),
            primaryOnClick = { updateOnClick(it as Item) },
            secondaryLabel = null,
            secondaryOnClick = null,
            deleteLabel = sRes(R.string.lsbs_delete),
            deleteOnClick = { deleteOnClick(it as Item) }
        )
    }
}

@Composable
fun ListFilters(
    filter: ListFilter,
    updateFilter: (ListFilter) -> Unit,
) {
    Column {
        SingleFilterSelection(
            name = sRes(R.string.ls_filter_byIsChecked),
            isSelected = filter.byIsChecked,
            updateIsSelected = { updateFilter(filter.copy(byIsChecked = !filter.byIsChecked))},
            filterOption = filter.byIsCheckedOption,
            updateFilterOption = { updateFilter(filter.copy(byIsCheckedOption = it)) },
        )
        SingleFilterSelection(
            name = sRes(R.string.ls_filter_byCategory),
            isSelected = filter.byCategory,
            updateIsSelected = { updateFilter(filter.copy(byCategory = !filter.byCategory))},
            filterOption = filter.byCategoryOption,
            updateFilterOption = { updateFilter(filter.copy(byCategoryOption = it)) },
        )
        SingleFilterSelection(
            name = sRes(R.string.ls_filter_byName),
            isSelected = filter.byName,
            updateIsSelected = { updateFilter(filter.copy(byName = !filter.byName))},
            filterOption = filter.byNameOption,
            updateFilterOption = { updateFilter(filter.copy(byNameOption = it)) },
        )
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    PreviewUtil.run {
        Preview {
            ListScreen(
                itemList = halfCheckedItemList,
                categories = emptyList(),
                emptyButtonOnClick = { },
                checkboxOnClick = { _, _ -> },
                showBottomSheet = false,
                updateShowBottomSheet = { },
                updateOnClick = { },
                deleteOnClick = { },
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
                categories = emptyList(),
                emptyButtonOnClick = { },
                checkboxOnClick = { _, _ -> },
                showBottomSheet = false,
                updateShowBottomSheet = { },
                updateOnClick = { },
                deleteOnClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun ListFiltersPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                ListFilters(
                    filter = ListFilter(),
                    updateFilter = { },
                )
            }
        }
    }
}