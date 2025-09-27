package com.supercatdev.catfeeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
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

        viewModel.signInAnonymously()

        setContent {
            AppNavigation()
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
                        // Remove labels by setting label to null
                        label = null,
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            // When tapping Home (Feeding), reset to initial Home state.
                            if (screen.route == Screen.Feeding.route) {
                                navController.navigate(Screen.Feeding.route) {
                                    // Clear up to start and recreate Home without restoring previous state
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            } else {
                                // Default behavior for other tabs (e.g., History)
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
