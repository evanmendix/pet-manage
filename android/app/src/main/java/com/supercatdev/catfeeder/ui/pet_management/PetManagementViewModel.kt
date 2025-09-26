package com.supercatdev.catfeeder.ui.pet_management
import android.util.Log
import com.supercatdev.catfeeder.data.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.PetRepository
import com.supercatdev.catfeeder.data.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.supercatdev.catfeeder.data.network.dto.CreatePetRequest

data class PetManagementUiState(
    val isLoading: Boolean = true,
    val pets: List<Pet> = emptyList(),
    val error: String? = null,
    val showAddPetDialog: Boolean = false,
    val petToDelete: Pet? = null,
    // A placeholder for the current user's ID. This would typically be fetched from an Auth repository.
    val currentUserId: String? = null

)

@HiltViewModel
class PetManagementViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetManagementUiState())
    val uiState: StateFlow<PetManagementUiState> = _uiState.asStateFlow()

    init {
        val currentUser = authRepository.getCurrentUser()
        _uiState.update { it.copy(currentUserId = currentUser?.uid) }
        fetchPets()
    }

    fun fetchPets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("PetManagementVM", "Starting to fetch pets...")
                val pets = petRepository.getPets()
                Log.d("PetManagementVM", "Successfully fetched ${pets.size} pets")
                pets.forEachIndexed { index, pet ->
                    Log.d("PetManagementVM", "Pet $index: ${pet.name} (${pet.id})")
                }
                _uiState.update { it.copy(isLoading = false, pets = pets) }
            } catch (e: Exception) {
                Log.e("PetManagementVM", "Failed to fetch pets: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onAddPetClicked() {
        _uiState.update { it.copy(showAddPetDialog = true) }
    }

    fun onAddPetDialogDismiss() {
        _uiState.update { it.copy(showAddPetDialog = false) }
    }

    fun addPet(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showAddPetDialog = false, isLoading = true) }
            try {
                val request = CreatePetRequest(name = name)
                petRepository.createPet(request)
                fetchPets() // Refresh the list
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onManagementChange(pet: Pet, isManaged: Boolean) {
        viewModelScope.launch {
            try {
                if (isManaged) {
                    petRepository.addManager(pet.id)
                } else {
                    petRepository.removeManager(pet.id)
                }
                fetchPets() // Refresh the list to show the change
            } catch (e: Exception) {
                 _uiState.update { it.copy(error = "Failed to update management status: ${e.message}") }
            }
        }
    }

    fun onDeletePet(pet: Pet) {
        _uiState.update { it.copy(petToDelete = pet) }
    }

    fun onDeleteCancelled() {
        _uiState.update { it.copy(petToDelete = null) }
    }

    fun onDeleteConfirmed() {
        _uiState.value.petToDelete?.let { pet ->
            viewModelScope.launch {
                try {
                    petRepository.deletePet(pet.id)
                    _uiState.update { it.copy(petToDelete = null) }
                    fetchPets() // Refresh list
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to delete pet: ${e.message}", petToDelete = null) }
                }
            }
        }
    }
}
