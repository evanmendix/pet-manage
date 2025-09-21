package com.supercatdev.catfeeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.supercatdev.catfeeder.ui.AuthState
import com.supercatdev.catfeeder.ui.MainViewModel
import com.supercatdev.catfeeder.ui.feeding.FeedingScreen
import com.supercatdev.catfeeder.ui.history.HistoryScreen
import com.supercatdev.catfeeder.ui.navigation.Screen
import com.supercatdev.catfeeder.ui.pet_management.PetManagementScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authState by viewModel.authState.collectAsState()

            when (val state = authState) {
                is AuthState.Idle, AuthState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AuthState.Success -> {
                    AppNavigation()
                }
                is AuthState.Error -> {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Authentication Error") },
                        text = { Text(state.message) },
                        confirmButton = {
                            Button(onClick = { finish() }) {
                                Text("Close App")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bottomBarItems = listOf(Screen.Feeding, Screen.History)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBarItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                        label = { screen.title?.let { Text(it) } },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feeding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feeding.route) {
                FeedingScreen(
                    onNavigateToPetManagement = { navController.navigate(Screen.PetManagement.route) }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.PetManagement.route) {
                PetManagementScreen()
            }
        }
    }
}
