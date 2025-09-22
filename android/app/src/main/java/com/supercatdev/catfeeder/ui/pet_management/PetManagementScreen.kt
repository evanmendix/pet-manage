package com.supercatdev.catfeeder.ui.pet_management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.supercatdev.catfeeder.data.model.Pet

import androidx.compose.ui.res.stringResource
import com.supercatdev.catfeeder.R

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetManagementScreen(
    viewModel: PetManagementViewModel = hiltViewModel(),
    onNavigateToEdit: (String) -> Unit = {},
    refreshSignal: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    // Trigger refresh when coming back from edit/delete screen
    LaunchedEffect(refreshSignal) {
        if (refreshSignal != null) {
            viewModel.fetchPets()
        }
    }

    if (uiState.showAddPetDialog) {
        AddPetDialog(
            onDismiss = { viewModel.onAddPetDialogDismiss() },
            onConfirm = { petName -> viewModel.addPet(petName) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.pet_management)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Toggle edit mode")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddPetClicked() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_pet))
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.pets) { pet ->
                    if (uiState.isEditMode) {
                        PetListClickableItem(
                            pet = pet,
                            onClick = { onNavigateToEdit(pet.id) }
                        )
                    } else {
                        PetManagementItem(
                            pet = pet,
                            isManaged = pet.managingUserIds.contains(uiState.currentUserId),
                            onManagementChange = { isManaged ->
                                viewModel.onManagementChange(pet, isManaged)
                            }
                        )
                    }
                }
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
    // This preview is now simpler as the ViewModel is the source of truth.
    // For a more complete preview, a fake ViewModel could be provided.
    PetManagementScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var petName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_pet)) },
        text = {
            TextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text(stringResource(R.string.pet_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (petName.isNotBlank()) {
                        onConfirm(petName)
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PetListClickableItem(
    pet: Pet,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pet.name, style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.Edit, contentDescription = null)
        }
    }
}
