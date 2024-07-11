package com.heyzeusv.yourlists.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.sRes

@Composable
fun AddScreen(
    addVM: AddViewModel,
    navController: NavHostController,
    topAppBarSetup: (TopAppBarState) -> Unit,
    fabSetup: (FabState) -> Unit,
) {
    val topAppBarTitle = sRes(AddDestination.title)

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
    AddScreen()
}

@Composable
fun AddScreen() {

}

@Preview
@Composable
private fun AddScreenPreview() {
    PreviewUtil.run {
        Preview {
            AddScreen()
        }
    }
}