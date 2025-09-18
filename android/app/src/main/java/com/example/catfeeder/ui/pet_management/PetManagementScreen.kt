package com.example.catfeeder.ui.pet_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catfeeder.data.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetManagementScreen(
    // Will be replaced with state from ViewModel
    pets: List<Pet>,
    currentUserId: String,
    onAddPetClick: () -> Unit,
    onManagementChange: (pet: Pet, isManaged: Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPetClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Pet")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pets) { pet ->
                PetManagementItem(
                    pet = pet,
                    isManaged = pet.managingUserIds.contains(currentUserId),
                    onManagementChange = { isManaged ->
                        onManagementChange(pet, isManaged)
                    }
                )
            }
        }
    }
}

@Composable
fun PetManagementItem(
    pet: Pet,
    isManaged: Boolean,
    onManagementChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pet.name, style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isManaged,
                onCheckedChange = onManagementChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetManagementScreenPreview() {
    val samplePets = listOf(
        Pet(id = "1", name = "Mimi", managingUserIds = listOf("user1")),
        Pet(id = "2", name = "Kiki", managingUserIds = emptyList()),
        Pet(id = "3", name = "Lulu", managingUserIds = listOf("user1", "user2"))
    )
    PetManagementScreen(
        pets = samplePets,
        currentUserId = "user1",
        onAddPetClick = {},
        onManagementChange = { _, _ -> }
    )
}
