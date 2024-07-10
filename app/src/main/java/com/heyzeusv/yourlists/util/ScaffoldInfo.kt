package com.heyzeusv.yourlists.util

data class ScaffoldInfo(
    val destination: Destination = OverviewDestination,
    val customTitle: String = "",
    val topBarNavPressed: () -> Unit = { },
    val topBarActionLeftPressed: () -> Unit = { },
    val topBarActionRightPressed: () -> Unit = { },
    val isFabDisplayed: Boolean = true,
    val fabAction: () -> Unit = { },
)