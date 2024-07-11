package com.heyzeusv.yourlists.util

data class TopAppBarState(
    val destination: Destination = OverviewDestination,
    val customTitle: String = "",
    val onNavPressed: () -> Unit = { },
    val onActionLeftPressed: () -> Unit = { },
    val onActionRightPressed: () -> Unit = { },
)

data class FabState(
    val isFabDisplayed: Boolean = true,
    val fabAction: () -> Unit = { },
)