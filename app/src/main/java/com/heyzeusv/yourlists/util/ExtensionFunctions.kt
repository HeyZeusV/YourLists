package com.heyzeusv.yourlists.util

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.database.models.ItemListWithItems

/**
 *  Load a string resource with formatting.
 *
 *  @param id The resource identifier.
 *  @param args The format arguments.
 *  @return The string data associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun sRes(@StringRes id: Int, vararg args: Any): String = stringResource(id, *args)

/**
 *  Create a [Painter] from an Android resource id.
 *
 *  @param id Resources object to query the image file from.
 *  @return [Painter] used for drawing the loaded resource.
 */
@Composable
fun pRes(@DrawableRes id: Int): Painter = painterResource(id)

/**
 *  Load a dimension resource.
 *
 *  @param id The resource identifier.
 *  @return The dimension value associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun dRes(@DimenRes id: Int): Dp = dimensionResource(id)

/**
 *  Load a integer resource.
 *
 *  @param id The resource identifier.
 *  @return The integer value associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun iRes(@IntegerRes id: Int): Int = integerResource(id)

/**
 *  Navigates app to given route.
 *
 *  @param route [Destination] to navigate to.
 */
fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    // pressing back from any screen would pop back stack to Overview
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) { saveState = true }
    // only 1 copy of a destination is ever created
    launchSingleTop = true
    // previous data and state is saved
    restoreState = true
}

/**
 *  Navigates to [ListDestination] while also passing an id to determine which [ItemListWithItems]
 *  to display.
 *
 *  @param listId Id of ItemList to display
 */
fun NavHostController.navigateToItemListWithId(listId: Long) {
    this.navigateSingleTopTo("${ListDestination.route}/$listId")
}

