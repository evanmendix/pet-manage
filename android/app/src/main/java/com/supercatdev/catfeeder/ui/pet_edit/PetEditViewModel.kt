package com.supercatdev.catfeeder.ui.pet_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.PetRepository
import com.supercatdev.catfeeder.data.network.dto.UpdatePetRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetEditUiState(
    val isLoading: Boolean = true,
    val petId: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val error: String? = null,
    val done: Boolean = false
)

@HiltViewModel
class PetEditViewModel @Inject constructor(
    private val repository: PetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetEditUiState())
    val uiState: StateFlow<PetEditUiState> = _uiState.asStateFlow()

    fun load(petId: String) {
        if (uiState.value.petId == petId && !uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, petId = petId) }
        viewModelScope.launch {
            try {
                val pets = repository.getAllPetsOrManaged()
                val pet = pets.find { it.id == petId }
                if (pet != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = pet.name,
                            photoUrl = pet.photoUrl,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Pet not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun save() {
        val state = _uiState.value
        if (state.petId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updatePet(state.petId, UpdatePetRequest(name = state.name, photoUrl = state.photoUrl))
                _uiState.update { it.copy(isLoading = false, done = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun delete() {
        val state = _uiState.value
        if (state.petId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deletePet(state.petId)
                _uiState.update { it.copy(isLoading = false, done = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
