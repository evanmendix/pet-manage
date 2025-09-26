package com.supercatdev.catfeeder.ui.pet_management

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.supercatdev.catfeeder.R
import com.supercatdev.catfeeder.data.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetManagementScreen(
    viewModel: PetManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showAddPetDialog) {
        AddPetDialog(
            onDismiss = { viewModel.onAddPetDialogDismiss() },
            onConfirm = { petName -> viewModel.addPet(petName) }
        )
    }

    uiState.petToDelete?.let { pet ->
        DeletePetDialog(
            petName = pet.name,
            onDismiss = { viewModel.onDeleteCancelled() },
            onConfirm = { viewModel.onDeleteConfirmed() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.pet_management)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
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
                    PetManagementItem(
                        pet = pet,
                        isManaged = pet.managingUserIds.contains(uiState.currentUserId),
                        onManagementChange = { isManaged ->
                            viewModel.onManagementChange(pet, isManaged)
                        },
                        onDeleteRequest = { viewModel.onDeletePet(pet) }
                    )
                }
            }
        }
    }
}

@Composable
fun PetManagementItem(
    pet: Pet,
    isManaged: Boolean,
    onManagementChange: (Boolean) -> Unit,
    onDeleteRequest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onDeleteRequest() }
                )
            }
    ) {
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
fun DeletePetDialog(
    petName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_pet_confirmation_title)) },
        text = { Text(stringResource(R.string.delete_pet_confirmation_message, petName)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
