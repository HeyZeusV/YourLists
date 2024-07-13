package com.heyzeusv.yourlists.util

import androidx.annotation.ArrayRes
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavBackStackEntry
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
 *  Load a string array resource .
 *
 *  @param id The resource identifier.
 *  @return The string array data associated with the resource.
 */
@Composable
@ReadOnlyComposable
fun saRes(@ArrayRes id: Int): Array<String> = stringArrayResource(id)

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
    // only 1 copy of a destination is ever created
    launchSingleTop = true
    // previous data and state is saved
    restoreState = true
}

/**
 *  Navigates to [ListDestination] while also passing an id and name to determine which
 *  [ItemListWithItems] to display.
 *
 *  @param listId Id of ItemList to display.
 *  @param listName Name of ItemList to display.
 */
fun NavHostController.navigateToItemList(listId: Long, listName: String?) {
    ListDestination.let {
        this.navigateSingleTopTo("${it.route}/$listId?${it.NAME_ARG}=$listName")
    }
}

/**
 *  Searches for the currently displayed [Destination] by comparing the current route in back stack
 *  to all available routes.
 *
 *  @return The current [Destination] displayed on screen.
 */
fun NavBackStackEntry?.currentDestination(): Destination {
    return Destinations.find {
        this?.destination?.route?.contains(it.route) ?: false
    } ?: OverviewDestination
}