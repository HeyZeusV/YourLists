package com.heyzeusv.yourlists.util

data class TopAppBarState(
    val destination: Destination = OverviewDestination,
    val title: String = "",
    val onNavPressed: () -> Unit = { },
    val onActionLeftPressed: () -> Unit = { },
    val onActionRightPressed: () -> Unit = { },
)

data class DrawerOnClicks(
    val importOnClick: () -> Unit = { },
    val exportOnClick: () -> Unit = { },
) {
    val onClickList = listOf(importOnClick, exportOnClick)
}

data class FabState(
    val isFabDisplayed: Boolean = true,
    val fabAction: () -> Unit = { },
)