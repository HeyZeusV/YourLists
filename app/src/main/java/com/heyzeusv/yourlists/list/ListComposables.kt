package com.heyzeusv.yourlists.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.heyzeusv.yourlists.util.ItemInfo
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.PreviewUtil
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
    val categories by listVM.categories.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = ListDestination,
                title = topAppBarTitle,
                onNavPressed = { navController.navigateUp() },
            )
        )
    }
    LaunchedEffect(key1 = itemList.items) {
        fabSetup(
            FabState(
                isFabDisplayed = itemList.items.isNotEmpty(),
                fabAction = { navController.navigateToAdd(itemList.itemList.itemListId) },
            )
        )
    }
    ListScreen(
        itemList = itemList,
        categories = categories,
        emptyButtonOnClick = { navController.navigateToAdd(itemList.itemList.itemListId) },
        checkboxOnClick = listVM::updateItemIsChecked,
        updateOnClick = listVM::updateItem,
        deleteOnClick = listVM::deleteItem
    )
}

@Composable
fun ListScreen(
    itemList: ItemListWithItems,
    categories: List<Category>,
    emptyButtonOnClick: () -> Unit,
    checkboxOnClick: (Item, (Boolean) -> Unit) -> Unit,
    updateOnClick: (Item) -> Unit,
    deleteOnClick: (Item) -> Unit,
) {
    val listState = rememberLazyListState()
    var isBottomSheetDisplayed by remember { mutableStateOf(false) }
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
            items(itemList.items) {
                ItemInfo(
                    item = it,
                    surfaceOnClick = {
                        selectedItem = it
                        isBottomSheetDisplayed = true
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
        isVisible = isBottomSheetDisplayed,
        updateIsVisible = { isBottomSheetDisplayed = it },
    ) {
        EditItemBottomSheetContent(
            closeBottomSheet = { isBottomSheetDisplayed = false },
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
                updateOnClick = { },
                deleteOnClick = { },
            )
        }
    }
}