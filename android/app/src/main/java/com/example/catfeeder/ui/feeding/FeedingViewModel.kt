package com.example.catfeeder.ui.feeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catfeeder.data.FeedingRepository
import com.example.catfeeder.data.PetRepository
import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.data.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedingUiState(
    val currentStatus: Feeding? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDuplicateFeedingDialog: Boolean = false,
    val managedPets: List<Pet> = emptyList(),
    val selectedPetId: String? = null,
    // A placeholder for the current user's ID. This would typically be fetched from an Auth repository.
    val currentUserId: String = "user1"
)

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedingUiState(isLoading = true))
    val uiState: StateFlow<FeedingUiState> = _uiState.asStateFlow()

    init {
        refreshAllData()
    }

    private fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch pets and filter for managed ones
                val allPets = petRepository.getPets()
                val managedPets = allPets.filter { it.managingUserIds.contains(_uiState.value.currentUserId) }

                // Update selected pet logic
                val selectedPetId = if (managedPets.size == 1) managedPets.first().id else null

                _uiState.update { it.copy(managedPets = managedPets, selectedPetId = selectedPetId) }

                // Fetch feeding status
                // TODO: The backend needs to be updated to fetch status for a specific pet or family.
                // For now, we continue to fetch the global status.
                feedingRepository.getCurrentStatus().onSuccess { status ->
                    _uiState.update { it.copy(isLoading = false, currentStatus = status) }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false, error = it.message) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectPet(petId: String) {
        _uiState.update { it.copy(selectedPetId = petId) }
    }

    fun addFeeding(type: String, force: Boolean = false) {
        val petId = uiState.value.selectedPetId ?: return // Do nothing if no pet is selected

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showDuplicateFeedingDialog = false) }
            // TODO: The 'Feeding' model on the backend needs a 'petId' field.
            val newFeeding = Feeding(userId = _uiState.value.currentUserId, timestamp = System.currentTimeMillis(), type = type)
            feedingRepository.addFeeding(newFeeding, force).onSuccess {
                refreshAllData()
            }.onFailure {
                if (it.message?.contains("Duplicate feeding detected") == true) {
                    _uiState.update { it.copy(isLoading = false, showDuplicateFeedingDialog = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = it.message) }
                }
            }
        }
    }

    fun dismissDuplicateFeedingDialog() {
        _uiState.update { it.copy(showDuplicateFeedingDialog = false) }
    }

    fun overwriteLastMeal(type: String) {
         val petId = uiState.value.selectedPetId ?: return // Do nothing if no pet is selected

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val newFeeding = Feeding(userId = _uiState.value.currentUserId, timestamp = System.currentTimeMillis(), type = type)
            feedingRepository.overwriteLastMeal(newFeeding).onSuccess {
                refreshAllData()
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = it.message) }
            }
        }
    }
}
