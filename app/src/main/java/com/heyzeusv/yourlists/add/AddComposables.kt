package com.heyzeusv.yourlists.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.database.models.DefaultItem
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.ItemInfo
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes

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
        itemLists = itemLists
    )
}

@Composable
fun AddScreen(
     defaultItemQuery: String,
     updateDefaultItemQuery: (String) -> Unit,
     defaultItems: List<DefaultItem>,
     itemLists: List<ItemListWithItems>,
) {
    val listState = rememberLazyListState()
    val maxLength = iRes(R.integer.name_max_length)

    Column(
        modifier = Modifier
            .padding(all = dRes(R.dimen.as_padding_all))
            .fillMaxSize()
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
                    style = MaterialTheme.typography.bodySmall
                )
            },
            singleLine = true,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(defaultItems) {
                ItemInfo(item = it)
            }
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
                itemLists = emptyList(),
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
                itemLists = emptyList(),
            )
        }
    }
}