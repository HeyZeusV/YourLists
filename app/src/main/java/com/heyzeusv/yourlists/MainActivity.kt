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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.yourlists.list.ListScreen
import com.heyzeusv.yourlists.list.ListViewModel
import com.heyzeusv.yourlists.overview.OverviewScreen
import com.heyzeusv.yourlists.overview.OverviewViewModel
import com.heyzeusv.yourlists.ui.theme.YourListsTheme
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.ScaffoldInfo
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
    var si by remember { mutableStateOf(ScaffoldInfo()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { YourListsTopAppBar(si = si) },
        floatingActionButton = {
            if (si.destination.fabText != 0) {
                ExtendedFloatingActionButton(
                    text = { Text(text = sRes(si.destination.fabText)) },
                    icon = {
                        Icon(
                            imageVector = si.destination.fabIcon,
                            contentDescription = sRes(si.destination.fabText)
                        )
                    },
                    modifier = Modifier.height(48.dp),
                    onClick = si.fabAction,
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = OverviewDestination.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(OverviewDestination.route) {
                val overviewVM: OverviewViewModel = hiltViewModel()
                OverviewScreen(
                    overviewVM = overviewVM,
                    navController = navController,
                    siSetUp = { si = it },
                )
            }
            composable(
                route = ListDestination.routeWithArg,
                arguments = ListDestination.arguments,
            ) { navBackStackEntry ->
                val listId = navBackStackEntry.arguments?.getLong(ListDestination.ID_ARG) ?: -1
                val listVM: ListViewModel = hiltViewModel<ListViewModel>().apply {
                    getItemListWithId(listId)
                }

                ListScreen(
                    listVM = listVM,
                    navController = navController,
                    siSetUp = { si = it },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourListsTopAppBar(si: ScaffoldInfo) {
    val title = if (si.destination.title != 0) sRes(si.destination.title) else si.customTitle
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            if (si.destination.navDescription != 0) {
                IconButton(onClick = si.topBarNavPressed) {
                    Icon(
                        imageVector = si.destination.navIcon,
                        contentDescription = sRes(si.destination.navDescription)
                    )
                }
            }
        },
        actions = {
            if (si.destination.actionLeftDescription != 0) {
                IconButton(onClick = si.topBarActionLeftPressed) {
                    Icon(
                        imageVector = si.destination.actionLeftIcon,
                        contentDescription = sRes(si.destination.actionLeftDescription)
                    )
                }
            }
            if (si.destination.actionRightDescription != 0) {
                IconButton(onClick = si.topBarActionRightPressed) {
                    Icon(
                        imageVector = si.destination.actionRightIcon,
                        contentDescription = sRes(si.destination.actionRightDescription)
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
        YourListsTopAppBar(si = ScaffoldInfo())
    }
}