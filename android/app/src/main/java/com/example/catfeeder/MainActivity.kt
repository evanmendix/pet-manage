package com.example.catfeeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.catfeeder.ui.feeding.FeedingScreen
import com.example.catfeeder.ui.history.HistoryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(
        stringResource(R.string.home) to Icons.Default.Home,
        stringResource(R.string.history) to Icons.Default.History
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.second, contentDescription = item.first) },
                        label = { Text(item.first) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> FeedingScreen(modifier = Modifier.padding(innerPadding))
            1 -> HistoryScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
