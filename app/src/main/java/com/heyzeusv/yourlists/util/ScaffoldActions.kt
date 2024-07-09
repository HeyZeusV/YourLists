package com.heyzeusv.yourlists.util

data class ScaffoldActions(
    val destination: Destination = OverviewDestination,
    val topBarNavPressed: () -> Unit = { },
    val topBarActionLeftPressed: () -> Unit = { },
    val topBarActionRightPressed: () -> Unit = { },
    val fabAction: () -> Unit = { }
)