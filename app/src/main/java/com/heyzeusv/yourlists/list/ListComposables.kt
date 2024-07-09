package com.heyzeusv.yourlists.list

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.ScaffoldActions

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
fun ListScreen(itemList: ItemListWithItems) {

}