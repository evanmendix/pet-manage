package com.supercatdev.catfeeder.ui.feeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supercatdev.catfeeder.data.AuthRepository
import com.supercatdev.catfeeder.data.FeedingRepository
import com.supercatdev.catfeeder.data.PetRepository
import com.supercatdev.catfeeder.data.model.Feeding
import com.supercatdev.catfeeder.data.model.Pet
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
    // Current user's ID, fetched from AuthRepository
    val currentUserId: String? = null
)

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val feedingRepository: FeedingRepository,
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedingUiState(isLoading = true))
    val uiState: StateFlow<FeedingUiState> = _uiState.asStateFlow()

    init {
        // Initialize current user id from AuthRepository, then refresh data
        val currentUserId = authRepository.getCurrentUser()?.uid
        _uiState.update { it.copy(currentUserId = currentUserId) }
        refreshAllData()
    }

    private fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch pets and filter for managed ones
                val allPets = petRepository.getPets()
                val userId = _uiState.value.currentUserId
                val managedPets = if (userId != null) {
                    allPets.filter { it.managingUserIds.contains(userId) }
                } else {
                    emptyList()
                }

                // Update selected pet logic
                val selectedPetId = if (managedPets.size == 1) managedPets.first().id else null

                _uiState.update { it.copy(managedPets = managedPets, selectedPetId = selectedPetId) }

                // Fetch feeding status
                // TODO: The backend needs to be updated to fetch status for a specific pet or family.
                // For now, we continue to fetch the global status.
                feedingRepository.getCurrentStatus().onSuccess { status ->
                    _uiState.update { it.copy(isLoading = false, currentStatus = status) }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
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
        val userId = uiState.value.currentUserId ?: return // Do nothing if no current user

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showDuplicateFeedingDialog = false) }
            // TODO: The 'Feeding' model on the backend needs a 'petId' field.
            val newFeeding = Feeding(userId = userId, timestamp = System.currentTimeMillis(), type = type)
            feedingRepository.addFeeding(newFeeding, force).onSuccess {
                refreshAllData()
            }.onFailure { throwable ->
                if (throwable.message?.contains("Duplicate feeding detected") == true) {
                    _uiState.update { it.copy(isLoading = false, showDuplicateFeedingDialog = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
            }
        }
    }

    fun dismissDuplicateFeedingDialog() {
        _uiState.update { it.copy(showDuplicateFeedingDialog = false) }
    }

    fun overwriteLastMeal(type: String) {
         val petId = uiState.value.selectedPetId ?: return // Do nothing if no pet is selected
         val userId = uiState.value.currentUserId ?: return // Do nothing if no current user

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val newFeeding = Feeding(userId = userId, timestamp = System.currentTimeMillis(), type = type)
            feedingRepository.overwriteLastMeal(newFeeding).onSuccess {
                refreshAllData()
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }
}
