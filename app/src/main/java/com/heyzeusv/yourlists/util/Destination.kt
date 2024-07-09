package com.heyzeusv.yourlists.util

import androidx.annotation.StringRes
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.heyzeusv.yourlists.R

interface Destination {
    val route: String
    @get:StringRes
    val title: Int get() = 0
}

object OverviewDestination: Destination {
    override val route: String = "overview"
    override val title: Int = R.string.app_name
}

object ListDestination: Destination {
    override val route: String = "list"

    const val ID_ARG = "list_id"
    var arguments = listOf(
        navArgument(ID_ARG) { type = NavType.IntType }
    )
    val routeWithArg = "$route/{$ID_ARG}"
}