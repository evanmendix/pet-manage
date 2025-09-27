package com.supercatdev.catfeeder.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.supercatdev.catfeeder.R
import com.supercatdev.catfeeder.data.model.Feeding
import com.supercatdev.catfeeder.data.model.Pet
import com.supercatdev.catfeeder.data.model.User
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
    // onNavigateToPetManagement: () -> Unit // This would be needed for real navigation
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.managedPets.isNotEmpty()) {
            // Top control row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PetSelector(
                    pets = uiState.managedPets,
                    selectedPetId = uiState.selectedPetId,
                    onPetSelected = viewModel::onPetSelected,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TimeRangeSelector(
                    selectedTimeRange = uiState.timeRange,
                    onTimeRangeSelected = viewModel::onTimeRangeSelected
                )
            }

            // Content area
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.feedings.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.feedings) { feeding ->
                            FeedingCard(feeding, uiState.users[feeding.userId])
                        }
                    }
                } else {
                    Text(text = stringResource(R.string.no_feeding_history))
                }
            }
        } else if (!uiState.isLoading) {
            // Empty state when no pets are managed
            EmptyState(
                message = stringResource(R.string.no_managed_pets_history_prompt),
                buttonText = stringResource(R.string.go_to_pet_management),
                onButtonClick = { /* onNavigateToPetManagement() */ }
            )
        }

        // Global loading indicator for initial pet fetch
        if (uiState.isLoading && uiState.managedPets.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        // Global error display
        uiState.error?.let {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.error_message, it),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String, buttonText: String, onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onButtonClick) {
            Text(text = buttonText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetSelector(
    pets: List<Pet>,
    selectedPetId: String?,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPetName = pets.find { it.id == selectedPetId }?.name ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedPetName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.selected_pet)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pets.forEach { pet ->
                DropdownMenuItem(
                    text = { Text(pet.name) },
                    onClick = {
                        onPetSelected(pet.id)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun TimeRangeSelector(selectedTimeRange: TimeRange, onTimeRangeSelected: (TimeRange) -> Unit) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeRangeButton(
            text = "24H",
            isSelected = selectedTimeRange == TimeRange.LAST_24_HOURS,
            onClick = { onTimeRangeSelected(TimeRange.LAST_24_HOURS) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        TimeRangeButton(
            text = "7D",
            isSelected = selectedTimeRange == TimeRange.LAST_7_DAYS,
            onClick = { onTimeRangeSelected(TimeRange.LAST_7_DAYS) }
        )
    }
}

@Composable
fun TimeRangeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
    Button(onClick = onClick, colors = colors) {
        Text(text)
    }
}

@Composable
fun FeedingCard(feeding: Feeding, feeder: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT+8")
            val formattedTime = sdf.format(Date(feeding.timestamp))
            val typeString = when (feeding.type) {
                "meal" -> stringResource(R.string.meal)
                "snack" -> stringResource(R.string.snack)
                else -> feeding.type
            }
            val feederName = feeder?.name ?: feeding.userId
            Text(text = stringResource(R.string.fed_by, feederName, typeString), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(R.string.feeding_time, formattedTime), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
