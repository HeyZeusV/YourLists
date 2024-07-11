package com.heyzeusv.yourlists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.yourlists.list.ListScreen
import com.heyzeusv.yourlists.list.ListViewModel
import com.heyzeusv.yourlists.overview.OverviewScreen
import com.heyzeusv.yourlists.overview.OverviewViewModel
import com.heyzeusv.yourlists.ui.theme.YourListsTheme
import com.heyzeusv.yourlists.util.Destination
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.currentDestination
import com.heyzeusv.yourlists.util.sRes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourListsTheme {
                YourLists()
            }
        }
    }
}

@Composable
fun YourLists(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    var topAppBarState by remember { mutableStateOf(TopAppBarState()) }
    var fabState by remember { mutableStateOf(FabState()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            YourListsTopAppBar(
                destination = currentBackStack.currentDestination(),
                title = topAppBarState.customTitle,
                onNavPressed = { topAppBarState.onNavPressed.invoke() },
                onActionLeftPressed = { topAppBarState.onActionLeftPressed.invoke() },
                onActionRightPressed = { topAppBarState.onActionRightPressed.invoke() },
            )
        },
        floatingActionButton = {
            if (fabState.isFabDisplayed) {
                ExtendedFloatingActionButton(
                    text = { Text(text = sRes(topAppBarState.destination.fabText)) },
                    icon = {
                        Icon(
                            imageVector = topAppBarState.destination.fabIcon,
                            contentDescription = sRes(topAppBarState.destination.fabText)
                        )
                    },
                    modifier = Modifier.height(48.dp),
                    onClick = fabState.fabAction,
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = OverviewDestination.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(OverviewDestination.route) {
                val overviewVM: OverviewViewModel = hiltViewModel()
                OverviewScreen(
                    overviewVM = overviewVM,
                    navController = navController,
                    topAppBarSetup = { topAppBarState = it },
                    fabSetup = { fabState = it },
                )
            }
            composable(
                route = ListDestination.routeWithArg,
                arguments = ListDestination.arguments,
            ) {
                val listVM: ListViewModel = hiltViewModel<ListViewModel>()

                ListScreen(
                    listVM = listVM,
                    navController = navController,
                    topAppBarSetup = { topAppBarState = it },
                    fabSetup = { fabState = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourListsTopAppBar(
    destination: Destination,
    title: String,
    onNavPressed: () -> Unit,
    onActionLeftPressed: () -> Unit,
    onActionRightPressed: () -> Unit,
) {
    val realTitle = title.ifBlank { sRes(destination.title) }
    TopAppBar(
        title = {
            Text(
                text = realTitle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            if (destination.navDescription != 0) {
                IconButton(onClick = onNavPressed) {
                    Icon(
                        imageVector = destination.navIcon,
                        contentDescription = sRes(destination.navDescription)
                    )
                }
            }
        },
        actions = {
            if (destination.actionLeftDescription != 0) {
                IconButton(onClick = onActionLeftPressed) {
                    Icon(
                        imageVector = destination.actionLeftIcon,
                        contentDescription = sRes(destination.actionLeftDescription)
                    )
                }
            }
            if (destination.actionRightDescription != 0) {
                IconButton(onClick = onActionRightPressed) {
                    Icon(
                        imageVector = destination.actionRightIcon,
                        contentDescription = sRes(destination.actionRightDescription)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Preview
@Composable
fun YourListsTopAppBarPreview() {
    PreviewUtil.Preview {
        YourListsTopAppBar(
            destination = OverviewDestination,
            title = sRes(OverviewDestination.title),
            onNavPressed = { },
            onActionLeftPressed = { },
            onActionRightPressed = { },
        )
    }
}