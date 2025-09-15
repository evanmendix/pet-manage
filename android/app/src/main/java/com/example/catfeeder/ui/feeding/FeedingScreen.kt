package com.example.catfeeder.ui.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FeedingScreen(
    viewModel: FeedingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Display current status
                    Text(text = "Current Status", style = MaterialTheme.typography.headlineMedium)
                    uiState.currentStatus?.let {
                        val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(it.timestamp))
                        Text("Last fed by: ${it.userId} (${it.type})")
                        Text("Time: $formattedTime")
                    } ?: Text("No feeding records yet.")

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Button(
                        onClick = { viewModel.addFeeding("meal") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Feed Meal")
                    }
                    Button(
                        onClick = { viewModel.addFeeding("snack") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Feed Snack")
                    }

                    // Display error if any
                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
