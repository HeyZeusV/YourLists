package com.heyzeusv.yourlists.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.heyzeusv.yourlists.database.models.ItemListWithItems
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

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
 *  Navigates to [AddDestination] while also passing an id and name to determine which
 *  [ItemListWithItems] to display.
 *
 *  @param listId Id of ItemList to add items to.
 */
fun NavHostController.navigateToAdd(listId: Long) {
    AddDestination.let {
        this.navigateSingleTopTo("${it.route}/$listId")
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
        this?.destination?.route?.split("/")?.first() == it.route
    } ?: OverviewDestination
}

fun TextFieldValue.toDouble(): Double {
    var chars = ""
    for (c: Char in this.text) { if (c.isDigit()) chars += c }

    return BigDecimal(chars).divide(BigDecimal(100), 2, RoundingMode.HALF_UP).toDouble()
}

fun TextFieldValue.formatTextAsDouble(decimalFormat: DecimalFormat): TextFieldValue {
    val textAsDouble = toDouble()
    return textAsDouble.toTextFieldValue(decimalFormat)
}

fun Double.toTextFieldValue(decimalFormat: DecimalFormat): TextFieldValue {
    val formattedString = decimalFormat.format(this)
    return TextFieldValue(
        text = formattedString,
        selection = TextRange(formattedString.length)
    )
}