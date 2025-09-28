package com.supercatdev.catfeeder.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector? = null, val title: String? = null) {
    object Feeding : Screen("feeding", Icons.Default.Home, "Home")
    object History : Screen("history", Icons.Default.History, "History")
    object PetManagement : Screen("pet_management", title = "Pet Management")
    object Settings : Screen("settings", title = "Settings")
}
