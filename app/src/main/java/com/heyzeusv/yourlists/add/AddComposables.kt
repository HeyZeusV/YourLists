package com.heyzeusv.yourlists.add

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.Category
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.EditItemBottomSheetContent
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.ListOptions
import com.heyzeusv.yourlists.util.BottomSheet
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.ItemInfo
import com.heyzeusv.yourlists.util.ListInfo
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes
import kotlinx.coroutines.launch

@Composable
fun AddScreen(
    addVM: AddViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
) {
    val topAppBarTitle = sRes(AddDestination.title)
    val defaultItemQuery by addVM.defaultItemQuery.collectAsStateWithLifecycle()
    val defaultItems by addVM.defaultItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val itemLists by addVM.itemLists.collectAsStateWithLifecycle()
    val categories by addVM.categories.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        topAppBarSetup(
            TopAppBarState(
                destination = AddDestination,
                title = topAppBarTitle,
                onNavPressed = { navController.navigateUp() }
            )
        )
    }
    LaunchedEffect(key1 = Unit) {
        fabSetup(FabState(isFabDisplayed = false))
    }
    AddScreen(
        defaultItemQuery = defaultItemQuery,
        updateDefaultItemQuery = { addVM.updateDefaultItemQuery(it) },
        defaultItems = defaultItems,
        categories = categories,
        saveAndAddOnClick = addVM::saveDefaultItemAndAddItem,
        addToListOnClick = addVM::addItem,
        deleteDefaultItemOnClick = addVM::deleteDefaultItem,
        itemLists = itemLists,
        addListButtonOnClick = addVM::addListWithOption,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddScreen(
    defaultItemQuery: String,
    updateDefaultItemQuery: (String) -> Unit,
    defaultItems: List<DefaultItem>,
    categories: List<Category>,
    saveAndAddOnClick: (DefaultItem) -> Unit,
    addToListOnClick: (DefaultItem) -> Unit,
    deleteDefaultItemOnClick: (DefaultItem) -> Unit,
    itemLists: List<ItemListWithItems>,
    addListButtonOnClick: (ItemListWithItems, ListOptions) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDefaultItem by remember { mutableStateOf(DefaultItem()) }
    var selectedItemList by remember { mutableStateOf(ItemListWithItems()) }

    BackHandler(enabled = showBottomSheet) {
        showBottomSheet = false
    }
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AddTabs.entries.forEachIndexed { index, addTabs ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = sRes(addTabs.nameId)) },
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> {
                    AddItemPage(
                        defaultItemQuery = defaultItemQuery,
                        updateDefaultItemQuery = updateDefaultItemQuery,
                        defaultItems = defaultItems,
                        updateSelectedDefaultItem = { selectedDefaultItem = it },
                        updateShowBottomSheet = { showBottomSheet = it },
                    )
                }
                1 -> {
                    AddListPage(
                        itemLists = itemLists,
                        itemListOnClick = {
                            selectedItemList = it
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }
    BottomSheet(
        isVisible = showBottomSheet,
        updateIsVisible = { showBottomSheet = it },
    ) {
        when (pagerState.currentPage) {
            0 -> {
                EditItemBottomSheetContent(
                    closeBottomSheet = {
                        updateDefaultItemQuery("")
                        showBottomSheet = false
                    },
                    selectedItem = selectedDefaultItem,
                    categories = categories,
                    primaryLabel = if (selectedDefaultItem.itemId == 0L) {
                        sRes(R.string.asbs_save_add)
                    } else {
                        sRes(R.string.asbs_update_add)
                    },
                    primaryOnClick = { saveAndAddOnClick(it as DefaultItem) },
                    secondaryLabel = sRes(R.string.asbs_add),
                    secondaryOnClick = { addToListOnClick(it as DefaultItem) },
                    deleteLabel = sRes(R.string.asbs_delete),
                    deleteOnClick = { deleteDefaultItemOnClick(it as DefaultItem) },
                )
            }
            1 -> {
                AddListBottomSheetContent(
                    itemList = selectedItemList,
                    buttonOnClick = { itemList, option ->
                        addListButtonOnClick(itemList, option)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddItemPage(
    defaultItemQuery: String,
    updateDefaultItemQuery: (String) -> Unit,
    defaultItems: List<DefaultItem>,
    updateSelectedDefaultItem: (DefaultItem) -> Unit,
    updateShowBottomSheet: (Boolean) -> Unit,
) {
    val listState = rememberLazyListState()

    val maxLength = iRes(R.integer.name_max_length)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.as_padding_all))
            .fillMaxSize(),
    ) {
        TextField(
            value = defaultItemQuery ,
            onValueChange = { if (it.length <= maxLength) updateDefaultItemQuery(it) },
            modifier = Modifier
                .padding(bottom = dRes(R.dimen.as_query_padding_bottom))
                .fillMaxWidth(),
            placeholder = { Text(text = sRes(R.string.as_item_query_placeholder)) },
            trailingIcon = {
                if (defaultItemQuery.isNotBlank()) {
                    IconButton(onClick = { updateDefaultItemQuery("") }) {
                        Icon(
                            painter = pRes(R.drawable.icon_cancel),
                            contentDescription = sRes(R.string.as_query_clear)
                        )
                    }
                }
            },
            supportingText = {
                Text(
                    text = "${defaultItemQuery.length}/$maxLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            singleLine = true,
        )
        AnimatedVisibility(visible = defaultItemQuery.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .padding(bottom = dRes(R.dimen.if_spacedBy_horizontal))
                    .fillMaxWidth()
                    .heightIn(dRes(R.dimen.if_height_min))
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        focusManager.clearFocus()
                        updateShowBottomSheet(true)
                        updateSelectedDefaultItem(DefaultItem(name = defaultItemQuery))
                    },
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = sRes(R.string.as_add_new, defaultItemQuery),
                        modifier = Modifier.padding(
                            horizontal = dRes(R.dimen.if_padding_horizontal),
                            vertical = dRes(R.dimen.if_padding_vertical),
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.as_list_spacedBy)),
        ) {
            items(
                items = defaultItems,
                key = { it.itemId },
            ) {
                ItemInfo(
                    modifier = Modifier.animateItemPlacement(),
                    item = it,
                    surfaceOnClick = {
                        focusManager.clearFocus()
                        updateSelectedDefaultItem(it)
                        updateShowBottomSheet(true)
                    },
                )
            }
        }
    }
}

@Composable
fun AddListPage(
    itemLists: List<ItemListWithItems>,
    itemListOnClick: (ItemListWithItems) -> Unit,
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.as_padding_all))
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.as_list_spacedBy))
    ) {
        Text(
            text = sRes(R.string.as_list_warning),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.as_list_spacedBy)),
        ) {
            items(itemLists) {
                ListInfo(
                    itemList = it,
                    itemListOnClick = itemListOnClick,
                    displayOptions = false,
                )
            }
        }
    }
}

@Composable
fun AddListBottomSheetContent(
    itemList: ItemListWithItems,
    buttonOnClick: (ItemListWithItems, ListOptions) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.bs_padding_all))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.bs_horizontal_spacedBy)),
    ) {
        Text(
            text = itemList.itemList.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = { buttonOnClick(itemList, ListOptions.COPY_ALL_AS_UNCHECKED) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(text = sRes(R.string.asbs_add_all_unchecked).uppercase())
        }
        Button(
            onClick = { buttonOnClick(itemList, ListOptions.COPY_ALL_AS_IS) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = sRes(R.string.asbs_add_all_as_is).uppercase())
        }
        OutlinedButton(
            onClick = { buttonOnClick(itemList, ListOptions.COPY_ONLY_UNCHECKED) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(text = sRes(R.string.asbs_add_only_uncheck).uppercase())
        }
    }
}

@Preview
@Composable
private fun AddScreenPreview() {
    PreviewUtil.run {
        Preview {
            AddScreen(
                defaultItemQuery = "Preview",
                updateDefaultItemQuery = { },
                defaultItems = defaultItemList,
                categories = emptyList(),
                saveAndAddOnClick = { },
                addToListOnClick = { },
                deleteDefaultItemOnClick = { },
                itemLists = emptyList(),
                addListButtonOnClick = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun AddScreenBlankQueryPreview() {
    PreviewUtil.run {
        Preview {
            AddScreen(
                defaultItemQuery = "",
                updateDefaultItemQuery = { },
                defaultItems = defaultItemList,
                categories = emptyList(),
                saveAndAddOnClick = { },
                addToListOnClick = { },
                deleteDefaultItemOnClick = { },
                itemLists = emptyList(),
                addListButtonOnClick = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun AddItemPagePreview() {
    PreviewUtil.run {
        Preview {
            AddItemPage(
                defaultItemQuery = "Preview",
                updateDefaultItemQuery = { },
                defaultItems = emptyList(),
                updateSelectedDefaultItem = { },
                updateShowBottomSheet = { },
            )
        }
    }
}

@Preview
@Composable
private fun AddListPagePreview() {
    PreviewUtil.run {
        Preview {
            AddListPage(
                itemLists = itemLists,
                itemListOnClick = { }
            )
        }
    }
}

@Preview
@Composable
private fun AddListBottomSheetPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                AddListBottomSheetContent(
                    itemList = halfCheckedItemList,
                    buttonOnClick = { _, _ -> },
                )
            }
        }
    }
}