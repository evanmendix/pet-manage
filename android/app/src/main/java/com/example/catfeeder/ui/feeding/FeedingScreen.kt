package com.example.catfeeder.ui.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catfeeder.R
import com.example.catfeeder.data.model.Pet
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedingViewModel = hiltViewModel(),
    onNavigateToPetManagement: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToPetManagement) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = stringResource(R.string.pet_management)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Box(contentAlignment = Alignment.Center) {
                if (uiState.isLoading && uiState.managedPets.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PetSelector(
                            pets = uiState.managedPets,
                            selectedPetId = uiState.selectedPetId,
                            onPetSelected = { viewModel.selectPet(it) },
                            onNavigateToPetManagement = onNavigateToPetManagement
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display current status
                        Text(text = stringResource(R.string.current_status), style = MaterialTheme.typography.headlineMedium)
                        uiState.currentStatus?.let {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("GMT+8")
                            val formattedTime = sdf.format(Date(it.timestamp))
                            val typeString = when (it.type) {
                                "meal" -> stringResource(R.string.meal)
                                "snack" -> stringResource(R.string.snack)
                                else -> it.type
                            }
                            Text(stringResource(R.string.last_fed_by, it.userId, typeString))
                            Text(stringResource(R.string.feeding_time, formattedTime))
                        } ?: Text(stringResource(R.string.no_feeding_records))

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Buttons
                        val actionsEnabled = uiState.selectedPetId != null
                        Button(
                            onClick = { viewModel.addFeeding("meal") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = actionsEnabled
                        ) {
                            Text(stringResource(R.string.feed_meal))
                        }
                        Button(
                            onClick = { viewModel.addFeeding("snack") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = actionsEnabled
                        ) {
                            Text(stringResource(R.string.feed_snack))
                        }

                        // Error Handling Dialogs
                        if (uiState.showDuplicateFeedingDialog) {
                            // ... (AlertDialog code remains the same)
                        }
                        uiState.error?.let {
                            // ... (Error text display remains the same)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetSelector(
    pets: List<Pet>,
    selectedPetId: String?,
    onPetSelected: (String) -> Unit,
    onNavigateToPetManagement: () -> Unit
) {
    when {
        pets.size > 1 -> {
            // TODO: Replace with a more visually appealing selector like a DropdownMenu or Chips
            Text("Select a pet:", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pets.forEach { pet ->
                    FilterChip(
                        selected = pet.id == selectedPetId,
                        onClick = { onPetSelected(pet.id) },
                        label = { Text(pet.name) }
                    )
                }
            }
        }
        pets.size == 1 -> {
            val pet = pets.first()
            Text("Feeding: ${pet.name}", style = MaterialTheme.typography.titleMedium)
        }
        else -> {
            Card(shape = RoundedCornerShape(8.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No pets are being managed.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateToPetManagement) {
                        Text("Go to Pet Management")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedingScreenPreview() {
    FeedingScreen(onNavigateToPetManagement = {})
}
