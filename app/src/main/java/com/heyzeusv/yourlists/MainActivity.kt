package com.heyzeusv.yourlists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.yourlists.add.AddScreen
import com.heyzeusv.yourlists.add.AddViewModel
import com.heyzeusv.yourlists.list.ListScreen
import com.heyzeusv.yourlists.list.ListViewModel
import com.heyzeusv.yourlists.overview.OverviewScreen
import com.heyzeusv.yourlists.overview.OverviewViewModel
import com.heyzeusv.yourlists.ui.theme.YourListsTheme
import com.heyzeusv.yourlists.util.AddDestination
import com.heyzeusv.yourlists.util.Destination
import com.heyzeusv.yourlists.util.DrawerOnClicks
import com.heyzeusv.yourlists.util.DrawerOption
import com.heyzeusv.yourlists.util.FabState
import com.heyzeusv.yourlists.util.ListDestination
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.TopAppBarState
import com.heyzeusv.yourlists.util.currentDestination
import com.heyzeusv.yourlists.util.dRes
import com.heyzeusv.yourlists.util.iRes
import com.heyzeusv.yourlists.util.pRes
import com.heyzeusv.yourlists.util.sRes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navController: NavHostController = rememberNavController(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBackStack by navController.currentBackStackEntryAsState()
    var topAppBarState by remember { mutableStateOf(TopAppBarState()) }
    var drawerOnClicks by remember { mutableStateOf(DrawerOnClicks()) }
    var fabState by remember { mutableStateOf(FabState()) }

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.85f)) {
                DrawerHeader()
                DrawerContent(
                    closeDrawer = { scope.launch { drawerState.close() } },
                    drawerOnClicks = drawerOnClicks,
                )
            }
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                YourListsTopAppBar(
                    destination = currentBackStack.currentDestination(),
                    title = topAppBarState.title,
                    onNavPressed = { topAppBarState.onNavPressed.invoke() },
                    onActionLeftPressed = { topAppBarState.onActionLeftPressed.invoke() },
                    onActionRightPressed = { topAppBarState.onActionRightPressed.invoke() },
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            val transitionDuration = iRes(R.integer.nav_transition_duration)
            NavHost(
                navController = navController,
                startDestination = OverviewDestination.route,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { slideInHorizontally(tween(transitionDuration)) { it } },
                exitTransition = { slideOutHorizontally(tween(transitionDuration)) { -it } },
                popEnterTransition = { slideInHorizontally(tween(transitionDuration)) { -it } },
                popExitTransition = { slideOutHorizontally(tween(transitionDuration)) { it } }
            ) {
                composable(OverviewDestination.route) {
                    val overviewVM: OverviewViewModel = hiltViewModel()
                    OverviewScreen(
                        overviewVM = overviewVM,
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        topAppBarSetup = {
                            topAppBarState =
                                it.copy(onNavPressed = { scope.launch { drawerState.open() } })
                        },
                        drawerSetup = { drawerOnClicks = it },
                        fabSetup = { fabState = it },
                    )
                }
                composable(
                    route = ListDestination.routeWithArg,
                    arguments = ListDestination.arguments,
                ) { bse ->
                    val listVM: ListViewModel = hiltViewModel()
                    val topAppBarTitle = bse.arguments?.getString(ListDestination.NAME_ARG) ?: ""
                    ListScreen(
                        listVM = listVM,
                        navController = navController,
                        topAppBarSetup = { topAppBarState = it },
                        fabSetup = { fabState = it },
                        topAppBarTitle = topAppBarTitle,
                    )
                }
                composable(
                    route = AddDestination.routeWithArg,
                    arguments = AddDestination.arguments,
                ) {
                    val addVM: AddViewModel = hiltViewModel()
                    AddScreen(
                        addVM = addVM,
                        navController = navController,
                        topAppBarSetup = { topAppBarState = it },
                        fabSetup = { fabState = it }
                    )
                }
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
    var isNavEnabled by remember { mutableStateOf(true) }
    val transitionDuration = iRes(R.integer.nav_transition_duration)

    LaunchedEffect(key1 = destination) {
        isNavEnabled = false
        delay(transitionDuration.toLong())
        isNavEnabled = true
    }
    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavPressed,
                enabled = isNavEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = destination.navIcon,
                    contentDescription = sRes(destination.navDescription)
                )
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

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = dRes(R.dimen.d_padding_vertical))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = pRes(R.drawable.ic_launcher_foreground),
            contentDescription = sRes(R.string.app_name),
            modifier = Modifier.scale(2f),
        )
        Text(
            text = sRes(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun DrawerContent(
    closeDrawer: () -> Unit,
    drawerOnClicks: DrawerOnClicks,
) {
    Column(
        modifier = Modifier.padding(top = dRes(R.dimen.d_content_padding_top)),
        verticalArrangement = Arrangement.spacedBy(dRes(R.dimen.d_content_spacedBy_vertical)),
    ) {
        DrawerOption.entries.forEachIndexed { index, drawerOption ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = sRes(drawerOption.nameId),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                selected = false,
                onClick = {
                    drawerOnClicks.onClickList[index]()
                    closeDrawer()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                icon = {
                    Icon(
                        painter = pRes(drawerOption.iconId),
                        contentDescription = sRes(drawerOption.nameId),
                    )
                }
            )
        }
    }
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

@Preview
@Composable
private fun DrawerHeaderPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                DrawerHeader()
            }
        }
    }
}

@Preview
@Composable
private fun DrawerContentPreview() {
    PreviewUtil.run {
        Preview {
            Surface {
                DrawerContent(
                    closeDrawer = { },
                    drawerOnClicks = DrawerOnClicks(),
                )
            }
        }
    }
}