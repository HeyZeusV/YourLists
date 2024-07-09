package com.heyzeusv.yourlists

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.yourlists.overview.OverviewScreen
import com.heyzeusv.yourlists.overview.OverviewViewModel
import com.heyzeusv.yourlists.ui.theme.YourListsTheme
import com.heyzeusv.yourlists.util.OverviewDestination
import com.heyzeusv.yourlists.util.PreviewUtil
import com.heyzeusv.yourlists.util.ScaffoldActions
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun YourLists(
    navController: NavHostController = rememberNavController()
) {
    val sa by remember { mutableStateOf(ScaffoldActions()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { YourListsTopAppBar(sa = sa) },
        floatingActionButton = {
            if (sa.destination.fabText != 0) {
                ExtendedFloatingActionButton(
                    text = { Text(text = sRes(sa.destination.fabText)) },
                    icon = {
                        Icon(
                            imageVector = sa.destination.fabIcon,
                            contentDescription = sRes(sa.destination.fabText)
                        )
                    },
                    modifier = Modifier.height(48.dp),
                    onClick = sa.fabAction,
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = OverviewDestination.route,
        ) {
            composable(OverviewDestination.route) {
                val overviewVm: OverviewViewModel = hiltViewModel()
                OverviewScreen(overviewVm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourListsTopAppBar(sa: ScaffoldActions) {
    TopAppBar(
        title = {
            Text(
                text = sRes(sa.destination.title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            if (sa.destination.navDescription != 0) {
                IconButton(onClick = sa.topBarNavPressed) {
                    Icon(
                        imageVector = sa.destination.navIcon,
                        contentDescription = sRes(sa.destination.navDescription)
                    )
                }
            }
        },
        actions = {
            if (sa.destination.actionLeftDescription != 0) {
                IconButton(onClick = sa.topBarActionLeftPressed) {
                    Icon(
                        imageVector = sa.destination.actionLeftIcon,
                        contentDescription = sRes(sa.destination.actionLeftDescription)
                    )
                }
            }
            if (sa.destination.actionRightDescription != 0) {
                IconButton(onClick = sa.topBarActionRightPressed) {
                    Icon(
                        imageVector = sa.destination.actionRightIcon,
                        contentDescription = sRes(sa.destination.actionRightDescription)
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
        YourListsTopAppBar(sa = ScaffoldActions())
    }
}