package com.heyzeusv.yourlists.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.heyzeusv.yourlists.R
import com.heyzeusv.yourlists.ui.icons.Filter

interface Destination {
    val route: String
    @get:StringRes
    val title: Int
    val navIcon: ImageVector
    @get:StringRes
    val navDescription: Int
    val actionLeftIcon: ImageVector
    @get:StringRes
    val actionLeftDescription: Int
    val actionRightIcon: ImageVector
    @get:StringRes
    val actionRightDescription: Int
    val fabIcon: ImageVector
    @get:StringRes
    val fabText: Int
}

object OverviewDestination: Destination {
    override val route: String = "overview"
    override val title: Int = R.string.app_name
    override val navIcon: ImageVector = Icons.AutoMirrored.Filled.List
    override val navDescription: Int = R.string.app_name
    override val actionLeftIcon: ImageVector = Blank
    override val actionLeftDescription: Int = 0
    override val actionRightIcon: ImageVector = Blank
    override val actionRightDescription: Int = 0
    override val fabIcon: ImageVector = Icons.Default.Add
    override val fabText: Int = R.string.os_fab
}

object ListDestination: Destination {
    override val route: String = "list"
    override val title: Int = R.string.ls_title
    override val navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    override val navDescription: Int = R.string.navigate_back
    override val actionLeftIcon: ImageVector = Blank
    override val actionLeftDescription: Int = 0
    override val actionRightIcon: ImageVector = Filter
    override val actionRightDescription: Int = R.string.ls_cdesc_filter
    override val fabIcon: ImageVector = Icons.Default.Add
    override val fabText: Int = R.string.ls_fab

    const val ID_ARG = "listId"
    const val NAME_ARG = "listName"
    var arguments = listOf(
        navArgument(ID_ARG) { type = NavType.LongType },
        navArgument(NAME_ARG) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        },
    )
    val routeWithArg = "$route/{$ID_ARG}?$NAME_ARG={$NAME_ARG}"
}

object AddDestination: Destination {
    override val route: String = "add"
    override val title: Int = R.string.as_title
    override val navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    override val navDescription: Int = R.string.navigate_back
    override val actionLeftIcon: ImageVector = Blank
    override val actionLeftDescription: Int = 0
    override val actionRightIcon: ImageVector = Blank
    override val actionRightDescription: Int = 0
    override val fabIcon: ImageVector = Blank
    override val fabText: Int = 0

    const val ID_ARG = "listId"
    var arguments = listOf(
        navArgument(ID_ARG) { type = NavType.LongType }
    )
    val routeWithArg = "$route/{$ID_ARG}"
}

val Destinations = listOf(OverviewDestination, ListDestination, AddDestination)

val Blank: ImageVector get() = materialIcon(name = "Filled.Blank") { materialPath { } }